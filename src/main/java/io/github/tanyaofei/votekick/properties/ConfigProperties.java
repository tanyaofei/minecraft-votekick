package io.github.tanyaofei.votekick.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Duration;

@Data
@Accessors(chain = true)
public class ConfigProperties {

    /**
     * 投票时大于这个比例则将玩家踢出去
     */
    private Double kickApproveFactor;

    /**
     * 投票时赞成数至少达到多少票才会将玩家踢出去
     */
    private Integer kickApproveMin;

    /**
     * 踢出持续时间
     */
    private Duration kickDuration;

    /**
     * 投票持续时间
     */
    private Duration voteDuration;

    /**
     * 发起投票冷却时间
     */
    private Duration serverCreateVoteCD;

    /**
     * 玩家发起投票冷却时间
     */
    private Duration playerCreateVoteCD;

    /**
     * 每个 IP 在一次投票中最多参与多少次
     */
    private int maxVotesPerIp;

    /**
     * 是否允许发起投票之后才登陆的玩家参与投票
     */
    private boolean allowLatePlayers;

    /**
     * 是否允许投票踢 OP
     */
    private boolean allowKickOp;

    /**
     * 是否每次有人投票都广播
     */
    private boolean broadcastOnEachVoted;

    public ConfigProperties(FileConfiguration file) {
        this.reload(file);
    }

    public void reload(FileConfiguration file) {
        this.kickApproveFactor = file.getDouble("config.kick-approve-factor", 0.5D);
        this.kickApproveMin = file.getInt("config.kick-approve-min", 0);
        this.kickDuration = Duration.ofSeconds(file.getLong("config.kick-seconds"), 30 * 60);
        this.voteDuration = Duration.ofSeconds(file.getLong("config.vote-seconds"), 10 * 60);
        this.serverCreateVoteCD = Duration.ofSeconds(file.getLong("config.server-create-cd-seconds"), 10 * 60);
        this.playerCreateVoteCD = Duration.ofSeconds(file.getLong("config.player-create-vote-cd-seconds"), 60 * 60);
        this.maxVotesPerIp = file.getInt("config.max-votes-per-ip", Integer.MAX_VALUE);
        this.allowLatePlayers = file.getBoolean("config.allow-late-players", true);
        this.allowKickOp = file.getBoolean("config.allow-kick-op", true);
        this.broadcastOnEachVoted = file.getBoolean("config.broadcast-on-each-voted", true);
    }

}
