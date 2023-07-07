package io.github.tanyaofei.votekick.manager.domain;


import io.github.tanyaofei.plugin.toolkit.progress.TimeProgress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

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
    private String creator;

    /**
     * 被投票的玩家
     */
    private String target;

    /**
     * 赞成的玩家
     */
    private Set<String> approved;

    /**
     * 反对的玩家
     */
    private Set<String> disapproved;

    /**
     * IP 参与度
     * key: ip
     * value: 投票次数
     */
    private Map<String, AtomicInteger> ipaddress;


    /**
     * 原因
     */
    private String reason;

    /**
     * 进度条
     */
    private TimeProgress progress;

}
