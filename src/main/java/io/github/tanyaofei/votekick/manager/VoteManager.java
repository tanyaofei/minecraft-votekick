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
import io.github.tanyaofei.votekick.util.IpAddressUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class VoteManager {

    private final static Logger log = Votekick.getLog();
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
            initiator.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_VoteExisted)
            );
            return;
        }

        var target = Votekick.getInstance().getServer().getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            initiator.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_PlayerNotFound, targetName)
            );
            if (Votekick.isDebug()) {
                log.info(String.format("拒绝发起投票, 因为玩家 %s 不在线", targetName));
            }
            return;
        }

        if (target.isOp() && !config.isAllowKickOp()) {
            initiator.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_NotAllowToKickOp)
            );
            if (Votekick.isDebug()) {
                log.info(String.format("拒绝发起投票, 因为被投玩家 %s 是 OP", targetName));
            }
            return;
        }

        if (!initiator.isOp()) {
            var now = LocalDateTime.now();
            var st = Optional
                    .ofNullable(serverLastVoteTimeRepository.getLastVotingTime(initiator.getServer()))
                    .orElse(LocalDateTime.MIN)
                    .plus(config.getServerCreateVoteCD());

            if (st.isAfter(now)) {
                initiator.sendMessage(
                        Votekick.getConfigManager()
                                .getLanguageProperties()
                                .format(LK.Error_ServerCreateTooManyVotes, Duration.between(now, st).toSeconds())
                );
                if (Votekick.isDebug()) {
                    log.info(String.format("拒绝发起投票, 服务器在 %s 之后才可以再次发起投票", st));
                }
                return;
            }

            var pt = Optional
                    .ofNullable(playerLastVoteTimeRepository.getLastCreateVotingTime(initiator))
                    .orElse(LocalDateTime.MIN)
                    .plus(config.getPlayerCreateVoteCD());

            if (pt.isAfter(now)) {
                initiator.sendMessage(
                        Votekick.getConfigManager()
                                .getLanguageProperties()
                                .format(LK.Error_PlayerCreateTooManyVotes,
                                        Duration.between(now, pt).toSeconds())
                );
                if (Votekick.isDebug()) {
                    log.info(String.format(
                            "拒绝发起投票，玩家 %s 在 %s 之后才可以再次发起投票", initiator.getName(), st)
                    );
                }
                return;
            }
        }

        var progress = initiator.getServer().createBossBar("Vote Kick", BarColor.GREEN, BarStyle.SOLID);
        for (var player : initiator.getServer().getOnlinePlayers()) {
            progress.addPlayer(player);
        }
        var vote = new KickVote()
                .setCreator(initiator.getName())
                .setTarget(target.getName())
                .setReason(reason)
                .setApprovePlayers(Collections.synchronizedSet(new HashSet<>()))
                .setDisapprovePlayers(Collections.synchronizedSet(new HashSet<>()))
                .setIpVotes(new ConcurrentHashMap<>())
                .setSnapshotBase(Votekick.getInstance().getServer().getOnlinePlayers().size())
                .setCreatedAt(LocalDateTime.now())
                .setProgress(progress);

        if (initiator instanceof Player p) {
            // 1. 发起投票的人如果是玩家则默认一票赞成
            // 2. 计算 IP 参与度
            // 3. 如果发起投票的人和被投的人不是同一个玩家则默认被投玩家一票反对
            vote.getApprovePlayers().add(initiator.getName());
            vote.getIpVotes().put(IpAddressUtils.getIpAddress(p), new AtomicInteger(1));
            if (!p.getName().equals(target.getName())) {
               vote.getDisapprovePlayers().add(target.getName());
               vote.getIpVotes().putIfAbsent(IpAddressUtils.getIpAddress(target), new AtomicInteger(1));
               vote.getIpVotes().get(IpAddressUtils.getIpAddress(target)).addAndGet(1);
            }
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

        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (vote.getTask().isCancelled()) {
                    timer.cancel();
                }
                var progress = vote.getProgress();
                var totalSeconds = (double) config.getVoteDuration().toSeconds();
                var remainSeconds = totalSeconds - (double) Duration.between(vote.getCreatedAt(), LocalDateTime.now()).toSeconds();
                progress.setProgress(remainSeconds / totalSeconds);
                progress.setTitle(((TextComponent) getInfo(vote)).content());
                progress.setColor(shouldKick(vote) ? BarColor.RED : BarColor.GREEN);
            }
        }, 0L, 1000L);

        vote.setTask(task);
        initiator.getServer()
                .broadcast(Votekick
                        .getConfigManager()
                        .getLanguageProperties()
                        .format(LK.VoteCreated,
                                initiator.getName(),
                                target.getName(),
                                vote.getReason(),
                                voteSeconds,
                                config.getKickDuration().toSeconds()
                        )
                );
    }

    public void vote(@NotNull Player player, @NotNull KickVote vote, @NotNull VoteChoice choice) {
        // 判断是否被取消
        if (vote.getTask().isCancelled()) {
            player.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_VoteNotFound)
            );
            if (Votekick.isDebug()) {
                log.info(String.format("拒绝 %s 投票, 因为该投票已经被取消", player.getName()));
            }
            return;
        }

        // 判断迟到用户
        var lastLoginAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(player.getLastLogin()),
                TimeZone.getDefault().toZoneId()
        );
        if (!config.isAllowLatePlayers() && lastLoginAt.isAfter(vote.getCreatedAt())) {
            player.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_PlayerLate)
            );
            if (Votekick.isDebug()) {
                log.info(String.format("拒绝 %s 投票, 投票发起于 %s 而玩家登陆于 %s", player.getName(), vote.getCreatedAt(), lastLoginAt));
            }
            return;
        }

        // 判断 IP 参与度
        var ip = IpAddressUtils.getIpAddress(player);
        if (!player.isOp()
                && !vote.getApprovePlayers().contains(player.getName())
                && !vote.getDisapprovePlayers().contains(player.getName())
        ) {
            vote.getIpVotes().putIfAbsent(ip, new AtomicInteger());
            if (vote.getIpVotes().get(ip).get() > config.getMaxVotesPerIp()) {
                player.sendMessage(
                        Votekick.getConfigManager()
                                .getLanguageProperties()
                                .format(LK.Error_IpTooManyVotes)
                );
                if (Votekick.isDebug()) {
                    log.info(String.format(
                            "拒绝 %s 投票, 因为他所在 IP %s 已经投过 %s 票",
                            player.getName(),
                            ip,
                            vote.getIpVotes().get(ip))
                    );
                }
                return;
            }
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

        // 增加 IP 参与度
        vote.getIpVotes().get(ip).incrementAndGet();

        player.sendMessage(
                Votekick.getConfigManager()
                        .getLanguageProperties()
                        .format(LK.PlayerVoted));
        if (config.isBroadcastOnEachVoted() || shouldKick(vote)) {
            Votekick.getInstance()
                    .getServer()
                    .sendActionBar(getInfo(vote));
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
        // 判断是否被取消
        if (vote.getTask().isCancelled()) {
            sender.sendMessage(Votekick.getConfigManager().getLanguageProperties().format(LK.VoteCanceled));
            return;
        }

        vote.getTask().cancel();
        vote.getProgress().removeAll();
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
        vote.getProgress().removeAll();

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
                .setExpires(now.plus(config.getKickDuration()))
                .setPlayerName(vote.getTarget());
        kickedRepository.saveOrUpdate(kicked);

        var player = Votekick.getInstance().getServer().getPlayer(kicked.getPlayerName());
        if (player != null) {
            player.kick(
                    Votekick.getConfigManager().getLanguageProperties().format(LK.Kick),
                    PlayerKickEvent.Cause.KICK_COMMAND
            );
        }

        Votekick.getInstance().getServer().broadcast(
                Votekick
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

    /**
     * 获取投票赞成比例
     *
     * @param vote 投票
     * @return 赞成比例
     */
    private double getVoteRate(KickVote vote) {
        if (vote.getTask().isCancelled()) {
            return 0.0D;
        }

        double base = getVoteBase(vote);
        return (base == 0)
                ? 1.0D
                : vote.getApprovePlayers().size() / base;
    }

    /**
     * 判断是否应该踢出
     *
     * @param vote 投票
     * @return 审丑应该踢
     */
    private boolean shouldKick(KickVote vote) {
        var rate = getVoteRate(vote);
        if (rate <= config.getKickApproveFactor()) {
            return false;
        }

        if (vote.getApprovePlayers().size() < config.getKickApproveMin()) {
            return false;
        }

        return true;
    }

    /**
     * 获取投票基数
     *
     * @param vote 投票
     * @return 基数
     */
    private Integer getVoteBase(@NotNull KickVote vote) {
        if (Votekick.getConfigManager().getConfigProperties().isAllowLatePlayers()) {
            return Votekick.getInstance().getServer().getOnlinePlayers().size();
        } else {
            return vote.getSnapshotBase();
        }
    }


}
