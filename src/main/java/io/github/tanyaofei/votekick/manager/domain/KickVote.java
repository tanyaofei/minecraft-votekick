package io.github.tanyaofei.votekick.manager.domain;


import io.github.tanyaofei.plugin.toolkit.progress.TimeProgress;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class KickVote {

    /**
     * 发起投票的玩家名称
     */
    @NotNull
    private String creator;

    /**
     * 被投票的玩家
     */
    @NotNull
    private String target;

    /**
     * 赞成的玩家
     */
    @NotNull
    @Getter(AccessLevel.PRIVATE)
    private Set<String> approved;

    /**
     * 反对的玩家
     */
    @NotNull
    @Getter(AccessLevel.PRIVATE)
    private Set<String> disapproved;

    /**
     * IP 参与度
     * key: ip
     * value: 投票次数
     */
    @NotNull
    private Map<String, AtomicInteger> ipaddress;

    /**
     * 原因
     */
    @NotNull
    private String reason;

    /**
     * 进度条
     */
    private TimeProgress progress;

    public @NotNull TimeProgress getProgress() {
        return progress;
    }

    public synchronized void addApproved(@NotNull String playerName) {
        approved.add(playerName);
        disapproved.remove(playerName);
    }

    public synchronized void addDisapproved(@NotNull String playerName) {
        approved.remove(playerName);
        disapproved.add(playerName);
    }

    public int getApprovedNums() {
        return approved.size();
    }

    public synchronized int[] getNums() {
        return new int[]{approved.size(), disapproved.size()};
    }

    public int getDisapproveNums() {
        return disapproved.size();
    }

    public synchronized double getProportion() {
        var total = approved.size() + disapproved.size();
        return (double) approved.size() / total;
    }

    public synchronized boolean isVote(@NotNull String playerName) {
        return approved.contains(playerName) || disapproved.contains(playerName);
    }



}
