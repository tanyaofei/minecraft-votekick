package io.github.tanyaofei.votekick.repository.model;

import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;

@Table("statistic")
public record Statistic(

        @Id("player_name")
        // 玩家名称
        String playerName,

        @Column("vote_count")
        // 次数
        Integer voteCount,

        @Column("kick_count")
        Integer kickCount

)  {
}
