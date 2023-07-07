package io.github.tanyaofei.votekick.repository.model;

import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;

import java.time.LocalDateTime;

@Table("kicked")
public record Kicked(

        // id
        @Id("id")
        Integer id,

        // 玩家名称
        @Column("player_name")
        String playerName,

        // 踢出时间
        @Column("started_at")
        LocalDateTime startedAt,

        // 结束踢出时间
        @Column("expired_at")
        LocalDateTime expiredAt

) {

}
