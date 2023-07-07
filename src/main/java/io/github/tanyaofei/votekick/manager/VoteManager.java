package io.github.tanyaofei.votekick.manager;

import com.google.common.base.Throwables;
import io.github.tanyaofei.plugin.toolkit.progress.ProgressType;
import io.github.tanyaofei.plugin.toolkit.progress.TimeProgress;
import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.manager.domain.KickVote;
import io.github.tanyaofei.votekick.properties.VotekickProperties;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import io.github.tanyaofei.votekick.repository.StatisticRepository;
import io.github.tanyaofei.votekick.repository.model.VoteChoice;
import io.github.tanyaofei.votekick.util.IpAddressUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class VoteManager {

    public final static VoteManager instance = new VoteManager();
    private final static Logger log = Votekick.getInstance().getLogger();
    private final Votekick votekick = Votekick.getInstance();
    private final KickedRepository kickedRepository = KickedRepository.instance;
    private final StatisticRepository statisticRepository = StatisticRepository.instance;
    private final VotekickProperties properties = VotekickProperties.instance;
    private final ConcurrentHashMap<String, LocalDateTime> playerLastVoteAt = new ConcurrentHashMap<>();
    private volatile KickVote current;
    private LocalDateTime serverLastVoteAt;

    public VoteManager() {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    var success = kickedRepository.cleanExpired();
                    log.info(String.format("已清理 %d 条过期的踢出名单", success));
                } catch (Throwable e) {
                    log.warning(Throwables.getStackTraceAsString(e));
                }
            }
        }, 0, 3600 * 1000);
    }

    public synchronized void createVote(
            @NotNull CommandSender sender,
            @NotNull String targetName,
            @Nullable String reason
    ) {
        reason = reason == null
                ? properties.getDefaultReason()
                : reason;

        var server = votekick.getServer();
        if (getCurrent() != null) {
            sender.sendMessage(text("已经在投票中了...", GRAY));
            return;
        }

        var target = sender.getServer().getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(text("玩家不在线...", GRAY));
            return;
        }

        if (!sender.isOp() && target.isOp() && !properties.isAllowKickOp()) {
            sender.sendMessage(text("服务器不允许投票踢 op ...", GRAY));
            return;
        }

        if (!sender.isOp()) {
            var now = LocalDateTime.now();

            var nextPlayerVotableTime = playerLastVoteAt
                    .getOrDefault(sender.getName(), LocalDateTime.MIN)
                    .plus(properties.getPlayerCd());

            if (nextPlayerVotableTime.isAfter(now)) {
                sender.sendMessage(text(String.format("你在 %s 秒之后才可以再次发起投票...", Duration.between(now, nextPlayerVotableTime).toSeconds()), GRAY));
                return;
            }

            var nextServerVotableTime = Optional
                    .ofNullable(serverLastVoteAt)
                    .orElse(LocalDateTime.MIN)
                    .plus(properties.getServerCd());

            if (nextServerVotableTime.isAfter(now)) {
                sender.sendMessage(String.format("服务器在 %s 秒之后才可以再次发起投票...", Duration.between(now, nextServerVotableTime).toSeconds()));
                return;
            }

        }

        var now = LocalDateTime.now();
        var bossBar = server.createBossBar("投票踢人", BarColor.GREEN, BarStyle.SOLID);
        var vote = new KickVote(
                sender.getName(),
                target.getName(),
                Collections.synchronizedSet(new HashSet<>()),
                Collections.synchronizedSet(new HashSet<>()),
                new ConcurrentHashMap<>(),
                reason,
                null
        );

        synchronized (this) {
            if (getCurrent() != null) {
                sender.sendMessage(text("已经在投票中了...", GRAY));
                return;
            }
            this.current = vote;
        }

        // 设定初始票数
        if (sender instanceof Player p) {
            vote.getApproved().add(p.getName());
            vote.getDisapproved().remove(p.getName());
        }
        vote.getApproved().remove(targetName);
        vote.getDisapproved().add(targetName);
        if (properties.isDefaultDisapprove()) {
            for (var player : server.getOnlinePlayers()) {
                if (!vote.getApproved().contains(player.getName())) {
                    vote.getDisapproved().add(player.getName());
                }
            }
        }

        var progress = new TimeProgress(
                now,
                now.plus(properties.getVoteDuration()),
                bossBar,
                500,
                ProgressType.REWARD,
                p -> {
                    bossBar.setTitle(String.format("投票踢 %s - %d 票赞成, %d 票反对", vote.getTarget(), vote.getApproved().size(), vote.getDisapproved().size()));
                    bossBar.setColor(shouldKick(vote) ? BarColor.RED : BarColor.GREEN);
                },
                () -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            finish(vote);
                        }
                    }.runTask(votekick);
                }
        );
        vote.setProgress(progress);

        server.broadcast(Component.textOfChildren(
                text(">>-------- 投票踢人 --------<<\n", RED),
                text("玩家 "), text(sender.getName(), DARK_GREEN), text(" 对玩家 "), text(targetName, DARK_GREEN), text(" 发起了投票踢出\n"),
                text("原因: "), text(reason).style(Style.style(GRAY, ITALIC)), Component.newline(),
                text("赞成请扣: "), text("/vk yes\n", RED),
                text("反对请扣: "), text("/vk no\n", DARK_GREEN),
                text("----------------------------------", RED)
        ));
        for (var player : server.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        serverLastVoteAt = now;
        playerLastVoteAt.put(sender.getName(), now);

        CompletableFuture.runAsync(() -> {
            try {
                statisticRepository.increaseVoteCountByPlayerName(targetName);
            } catch (Throwable e) {
                log.warning(Throwables.getStackTraceAsString(e));
            }
        });
    }

    public void vote(@NotNull Player player, @NotNull KickVote vote, @NotNull VoteChoice choice) {
        if (vote != current) {
            player.sendMessage(text("投票结束了...", GRAY));
            return;
        }

        // 判断 IP 参与度
        var ip = IpAddressUtils.getIpAddress(player);
        if (!player.isOp()
                && !vote.getApproved().contains(player.getName())
                && !vote.getDisapproved().contains(player.getName())
        ) {
            vote.getIpaddress().putIfAbsent(ip, new AtomicInteger());
            if (vote.getIpaddress().get(ip).get() > properties.getMaxVotesPerIp()) {
                player.sendMessage(text("你所在的 IP 不能投出更多的票...", GRAY));
                return;
            }
            vote.getIpaddress().get(ip).incrementAndGet();
        }

        var playerName = player.getName();
        switch (choice) {
            case APPROVE -> {
                vote.getApproved().add(playerName);
                vote.getDisapproved().remove(playerName);
            }
            case DISAPPROVE -> {
                vote.getApproved().remove(playerName);
                vote.getDisapproved().add(playerName);
            }
        }

        player.sendMessage(text("感谢你投出宝贵的一票, 阿里嘎多～", DARK_GREEN));
    }

    @Nullable
    public KickVote getCurrent() {
        return current;
    }

    public void cancel(CommandSender sender, KickVote vote) {
        if (current == vote) {
            current = null;
        }
        vote.getProgress().cancel();
        sender.getServer()
                .broadcast(Component.textOfChildren(
                        text("本次投票踢人已被 ", RED),
                        text(sender.getName(), NamedTextColor.GOLD),
                        text(" 取消", RED)
                ));
    }

    private void finish(KickVote vote) {
        current = null;
        vote.getProgress().cancel();

        var shouldKick = shouldKick(vote);
        votekick
                .getServer()
                .broadcast(Component.textOfChildren(
                        text(">>-------- 投票踢人 --------<<\n", RED),
                        text("赞成: "), text(vote.getApproved().size(), RED), text(" 票\n"),
                        text("反对: "), text(vote.getDisapproved().size(), GREEN), text(" 票\n"),
                        text("结果: "), shouldKick ? text("拜拜了嘞", RED) : text("再让你苟一会\n", DARK_GREEN),
                        text("----------------------------------", RED)
                ));

        if (!shouldKick) {
            return;
        }


        var target = vote.getTarget();
        var now = LocalDateTime.now();
        kickedRepository.insert(new io.github.tanyaofei.votekick.repository.model.Kicked(
                null,
                vote.getTarget(),
                now,
                now.plus(properties.getKickDuration())
        ));

        var player = votekick.getServer().getPlayer(target);
        if (player != null && player.isOnline()) {
            player.kick(text("你被投票踢出了服务器, 原因是: " + vote.getReason() + "\n:(", RED), PlayerKickEvent.Cause.KICK_COMMAND);
            votekick.getServer().broadcast(text(String.format("[感谢大哥 %s 送的火箭, 玩家 %s 被踢出了服务器!]", vote.getCreator(), target)).style(Style.style(RED, ITALIC)));
        }

        CompletableFuture.runAsync(() -> {
            try {
                statisticRepository.increaseKickCountByPlayerName(target);
            } catch (Throwable e) {
                log.warning(Throwables.getStackTraceAsString(e));
            }
        });

    }

    /**
     * 获取投票赞成比例
     *
     * @param vote 投票
     * @return 赞成比例
     */
    private double getRate(KickVote vote) {
        var a = vote.getApproved().size();
        var b = vote.getDisapproved().size();
        var total = a + b;
        return total == 0 ? 0 : ((double) a) / total;
    }

    /**
     * 判断是否应该踢出
     *
     * @param vote 投票
     * @return 是否应该踢出
     */
    private boolean shouldKick(KickVote vote) {
        return vote.getApproved().size() >= properties.getKickAtLeast()
                && getRate(vote) > properties.getKickFactor();
    }

}
