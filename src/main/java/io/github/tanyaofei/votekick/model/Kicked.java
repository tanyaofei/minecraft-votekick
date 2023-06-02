package io.github.tanyaofei.votekick.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Kicked {

    /**
     * 玩家 ID
     */
    private String playerName;

    /**
     * 踢出时间
     */
    private LocalDateTime kickAt;

    /**
     * 提出原因
     */
    private String reason;

    /**
     * 恢复时间
     */
    private LocalDateTime expires;

}
