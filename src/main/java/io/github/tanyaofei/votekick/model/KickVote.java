package io.github.tanyaofei.votekick.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Accessors(chain = true)
public class KickVote {

    /**
     * 任务 ID
     */
    private BukkitTask task;

    /**
     * 发起人
     */
    private String creator;

    /**
     * 被投票的人
     */
    private String target;

    /**
     * 投票标题
     */
    private String reason = "no reason";

    /**
     * ip 参与度
     */
    private ConcurrentMap<String, AtomicInteger> ipVotes;

    /**
     * 赞成的玩家
     */
    private Set<String> approvePlayers;

    /**
     * 反对的玩家
     */
    private Set<String> disapprovePlayers;

    /**
     * 投票发起时间
     */
    private LocalDateTime createdAt;

    /**
     * 发起投票时在线玩家数
     */
    private Integer snapshotBase;

}
