package io.github.tanyaofei.votekick.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Kicking {

    /**
     * 投票原因
     */
    private String reason;

    /**
     * 赞成数
     */
    private Integer inFavor;

    /**
     * 反对数
     */
    private Integer against;

    /**
     * 弃权数
     */
    private Integer abstentions;

    /**
     * 投票时间
     */
    private LocalDateTime voteTime;

    /**
     * 投票发起人
     */
    private String votingInitiator;

}
