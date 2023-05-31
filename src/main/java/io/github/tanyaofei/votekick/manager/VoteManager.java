package io.github.tanyaofei.votekick.manager;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.model.KickVote;
import io.github.tanyaofei.votekick.model.Kicked;
import io.github.tanyaofei.votekick.model.VoteChoice;
import io.github.tanyaofei.votekick.properties.ConfigProperties;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import io.github.tanyaofei.votekick.repository.PlayerLastVoteTimeRepository;
import io.github.tanyaofei.votekick.repository.ServerLastVoteTimeRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {
    private final static int TICKS_PER_SECOND = 20;
    private final PlayerLastVoteTimeRepository playerLastVoteTimeRepository = PlayerLastVoteTimeRepository.getInstance();
    private final ServerLastVoteTimeRepository serverLastVoteTimeRepository = ServerLastVoteTimeRepository.getInstance();
    private final KickedRepository kickedRepository = KickedRepository.getInstance();
    private final ConfigProperties config;
    private volatile KickVote current;

    public VoteManager(ConfigProperties config) {
        this.config = config;
    }

    public synchronized void createVote(CommandSender initiator, String targetName, String reason) {
        if (current != null) {
            initiator.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_VoteExisted)
            );
            return;
        }

        var target = Votekick.getInstance().getServer().getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            initiator.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_PlayerNotFound, targetName)
            );
            return;
        }

        if (target.isOp() && !config.isAllowKickOp()) {
            initiator.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_NotAllowToKickOp)
            );
            return;
        }

        if (!initiator.isOp()) {
            var now = LocalDateTime.now();
            var st = Optional
                    .ofNullable(serverLastVoteTimeRepository.getLastVotingTime(initiator.getServer()))
                    .orElse(LocalDateTime.MIN)
                    .plus(config.getServerCreateVoteCD());

            if (st.isAfter(now)) {
                initiator.sendMessage(Votekick
                        .getConfigManager()
                        .getLanguageProperties()
                        .format(LK.Error_ServerCreateTooManyVotes, Duration.between(now, st).toSeconds())
                );
                return;
            }

            var pt = Optional
                    .ofNullable(playerLastVoteTimeRepository.getLastCreateVotingTime(initiator))
                    .orElse(LocalDateTime.MIN)
                    .plus(config.getPlayerCreateVoteCD());

            if (pt.isAfter(now)) {
                initiator.sendMessage(Votekick
                        .getConfigManager()
                        .getLanguageProperties()
                        .format(LK.Error_PlayerCreateTooManyVotes, Duration.between(now, pt).toSeconds())
                );
                return;
            }
        }

        var vote = new KickVote()
                .setCreator(initiator.getName())
                .setTarget(target.getName())
                .setReason(reason)
                .setApprovePlayers(Collections.synchronizedSet(new HashSet<>()))
                .setDisapprovePlayers(Collections.synchronizedSet(new HashSet<>()))
                .setIpVotes(new ConcurrentHashMap<>())
                .setSnapshotBase(Votekick.getInstance().getServer().getOnlinePlayers().size())
                .setCreatedAt(LocalDateTime.now());

        vote.getApprovePlayers().add(initiator.getName());
        if (!(initiator instanceof Player p && p.getName().equals(target.getName()))) {
            // 如果发起人不是被投的人，则自动将被投人加入到反对列中
            vote.getDisapprovePlayers().add(target.getName());
        }
        current = vote;

        var voteSeconds = config.getVoteDuration().toSeconds();
        var task = Votekick
                .getInstance()
                .getServer()
                .getScheduler()
                .runTaskLater(
                        Votekick.getInstance(),
                        () -> this.finish(vote),
                        config.getVoteDuration().toSeconds() * TICKS_PER_SECOND
                );


        vote.setTask(task);
        initiator
                .getServer()
                .broadcast(Votekick
                        .getConfigManager()
                        .getLanguageProperties()
                        .format(LK.VoteCreated,
                                initiator.getName(),
                                target.getName(),
                                vote.getReason(),
                                voteSeconds,
                                config.getKickDuration().toSeconds())
                );
    }

    public void vote(@NotNull Player player, @NotNull KickVote vote, @NotNull VoteChoice choice) {
        if (vote.getTask().isCancelled()) {
            player.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_VoteNotFound)
            );
            return;
        }
        if (!config.isAllowLatePlayers() && LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getLastLogin()), TimeZone.getDefault().toZoneId()).isAfter(vote.getCreatedAt())) {
            player.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_PlayerLate)
            );
            return;
        }

        if (vote.getIpVotes().getOrDefault(Optional.ofNullable(player.getAddress()).map(InetSocketAddress::getHostString).orElse(null), 0) > config.getMaxVotesPerIp()) {
            player.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_IpTooManyVotes)
            );
            return;
        }

        var playerName = player.getName();
        switch (choice) {
            case APPROVE -> {
                vote.getApprovePlayers().add(playerName);
                vote.getDisapprovePlayers().remove(player.getName());
            }
            case DISAPPROVE -> {
                vote.getApprovePlayers().remove(playerName);
                vote.getDisapprovePlayers().add(playerName);
            }
        }
        player.sendMessage(Votekick
                .getConfigManager()
                .getLanguageProperties()
                .format(LK.PlayerVoted));
        if (config.isBroadcastOnEachVoted() || shouldKick(vote)) {
            Votekick
                    .getInstance()
                    .getServer()
                    .broadcast(getInfo(vote));
        }
    }

    public Kicked getKicked(Player player) {
        return kickedRepository.getByPlayer(player);
    }

    public void unkick(Player player) {
        kickedRepository.removeByPlayer(player);
    }

    @Nullable
    public KickVote getCurrent() {
        return current;
    }

    public void cancel(CommandSender sender, KickVote vote) {
        if (vote.getTask().isCancelled()) {
            sender.sendMessage(Component.text("[Votekick] Vote has been canceled"));
            return;
        }

        vote.getTask().cancel();

        current = null;
        Votekick.getInstance().getServer().broadcast(Votekick
                .getConfigManager()
                .getLanguageProperties().format(
                        LK.VoteCanceled,
                        sender.getName(),
                        vote.getCreator(),
                        vote.getTarget()
                ));
    }

    private void finish(KickVote vote) {
        current = null;
        serverLastVoteTimeRepository.saveOrUpdate(
                Votekick.getInstance().getServer(),
                LocalDateTime.now()
        );
        playerLastVoteTimeRepository.saveOrUpdate(
                vote.getCreator(),
                LocalDateTime.now()
        );

        if (!shouldKick(vote)) {
            Votekick.getInstance().getServer().broadcast(Votekick
                    .getConfigManager()
                    .getLanguageProperties().format(
                            LK.VoteFinishedNotKick,
                            vote.getCreator(),
                            vote.getTarget(),
                            vote.getApprovePlayers().size(),
                            vote.getApprovePlayers().size(),
                            getVoteBase(vote)
                    ));
            return;
        }

        var now = LocalDateTime.now();
        var kicked = new Kicked()
                .setReason(vote.getReason())
                .setKickAt(now)
                .setCanJoinIn(now.plus(config.getKickDuration()))
                .setPlayerName(vote.getTarget());
        kickedRepository.saveOrUpdate(kicked);

        var player = Votekick.getInstance().getServer().getPlayer(kicked.getPlayerName());
        if (player != null && player.isOnline()) {
            player.kick(
                    Votekick.getConfigManager().getLanguageProperties().format(LK.Kick),
                    PlayerKickEvent.Cause.KICK_COMMAND
            );
        }

        Votekick.getInstance().getServer().broadcast(Votekick
                .getConfigManager()
                .getLanguageProperties().format(
                        LK.VoteFinishedKick,
                        vote.getCreator(),
                        vote.getTarget(),
                        vote.getApprovePlayers().size(),
                        vote.getApprovePlayers().size(),
                        getVoteBase(vote)
                ));
    }

    public Component getInfo(KickVote vote) {
        return Votekick
                .getConfigManager()
                .getLanguageProperties().format(
                        LK.VoteInfo,
                        vote.getCreator(),
                        vote.getTarget(),
                        vote.getApprovePlayers().size(),
                        vote.getDisapprovePlayers().size(),
                        config.getVoteDuration().minus(Duration.between(vote.getCreatedAt(), LocalDateTime.now())).toSeconds()
                );

    }

    private boolean shouldKick(KickVote vote) {
        if (vote.getTask().isCancelled()) {
            return false;
        }

        double base = config.isAllowLatePlayers()
                ? Votekick.getInstance().getServer().getOnlinePlayers().size()
                : vote.getSnapshotBase();

        double rate = (base == 0)
                ? 1.0D
                : vote.getApprovePlayers().size() / base;

        if (rate <= config.getKickApproveFactor()) {
            return false;
        }

        if (vote.getApprovePlayers().size() < config.getKickApproveMin()) {
            return false;
        }

        return true;
    }

    private Integer getVoteBase(@NotNull KickVote vote) {
        if (Votekick.getConfigManager().getConfigProperties().isAllowLatePlayers()) {
            return Votekick.getInstance().getServer().getOnlinePlayers().size();
        } else {
            return vote.getSnapshotBase();
        }
    }


}
