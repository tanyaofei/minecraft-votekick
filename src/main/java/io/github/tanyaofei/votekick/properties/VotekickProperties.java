package io.github.tanyaofei.votekick.properties;


import io.github.tanyaofei.plugin.toolkit.io.IOUtil;
import io.github.tanyaofei.votekick.Votekick;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Objects;
import java.util.logging.Logger;

@Getter
@Data
public class VotekickProperties {


    public final static VotekickProperties instance;
    private final static Logger log;
    private final static String VERSION = "1";

    static {
        log = Votekick.getInstance().getLogger();
        instance = new VotekickProperties();
    }

    private final Votekick votekick = Votekick.getInstance();

    /**
     * 踢出比例
     */
    private double kickFactor;

    /**
     * 至少赞成达到多少票才踢出玩家
     */
    private int kickAtLeast;

    /**
     * 踢出时间
     */
    private Duration kickDuration;

    /**
     * 投票持续时间
     */
    private Duration voteDuration;

    /**
     * 服务器发起投票 CD
     */
    private Duration serverCd;

    /**
     * 玩家发起投票 CD
     */
    private Duration playerCd;

    /**
     * 一个 IP 最多投多少票
     */
    private int maxVotesPerIp;

    /**
     * 是否允许踢 OP
     */
    private boolean allowKickOp;

    /**
     * OP 被踢后能否重新加入游戏
     */
    private boolean allowOpRejoin;


    /**
     * 默认反对
     */
    private boolean defaultDisapprove;

    /**
     * 默认理由
     */
    private String defaultReason;

    public VotekickProperties() {
        var folder = Votekick.getInstance().getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("创建配置文件目录失败");
        }

        var file = new File(folder, "config.yml");
        if (!file.exists()) {
            try (var in = Objects.requireNonNull(Votekick.class.getClassLoader().getResource("config.yml")).openStream()) {
                IOUtil.copy(in, new FileWriter(file));
                log.info("创建默认配置文件成功");
            } catch (IOException e) {
                throw new UncheckedIOException("创建默认配置文件失败", e);
            }
        }

        reload();
    }

    public void reload() {
        votekick.reloadConfig();
        var config = votekick.getConfig();
        if (!VERSION.equals(config.getString("version", "").trim())) {
            log.warning("配置文件版本与插件版本不匹配，部分配置可能没有生效，请删掉配置文件后重新启动生成");
        }

        this.kickFactor = config.getDouble("kick-factor", 0.5D);
        this.kickAtLeast = config.getInt("kick-at-least", 0);
        this.kickDuration = Duration.ofSeconds(config.getInt("kick-duration", 1800));
        this.voteDuration = Duration.ofSeconds(config.getInt("kick-duration", 180));
        this.serverCd = Duration.ofSeconds(config.getInt("server-cd", 300));
        this.playerCd = Duration.ofSeconds(config.getInt("player-cd"), 1800);
        this.maxVotesPerIp = maybeMax(config.getInt("max-votes-per-ip", 1));
        this.allowKickOp = config.getBoolean("allow-kick-op", true);
        this.allowOpRejoin = config.getBoolean("allow-op-rejoin", true);
        this.defaultReason = config.getString("default-reason", "这个玩家太懒居然没写理由");
        this.defaultDisapprove = config.getBoolean("default-disapprove", false);
    }

    public static int maybeMax(int value) {
        return value < 1 ? Integer.MAX_VALUE : value;
    }

}
