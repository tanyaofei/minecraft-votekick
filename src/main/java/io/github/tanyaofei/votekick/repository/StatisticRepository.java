package io.github.tanyaofei.votekick.repository;

import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.repository.model.Statistic;
import io.github.tanyaofei.votekick.repository.model.TopType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class StatisticRepository extends AbstractRepository<Statistic> {

    public final static StatisticRepository instance = new StatisticRepository(Votekick.getInstance());

    public StatisticRepository(Plugin plugin) {
        super(plugin);
    }

    public int increaseVoteCountByPlayerName(@NotNull String playerName) {
        var sql = """
                insert or replace into statistic (player_name, vote_count, kick_count)
                values (
                    ?,
                    ifnull((select vote_count from statistic where player_name = ?), 0) + 1,
                    ifnull((select kick_count from statistic where player_name = ?), 0)
                )
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, playerName);
            stm.setObject(2, playerName);
            stm.setObject(3, playerName);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int increaseKickCountByPlayerName(@NotNull String playerName) {
        var sql = """
                insert or replace into statistic (player_name, vote_count, kick_count)
                values (
                    ?,
                    ifnull((select vote_count from statistic where player_name = ?), 0),
                    ifnull((select kick_count from statistic where player_name = ?), 0) + 1
                )
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, playerName);
            stm.setObject(2, playerName);
            stm.setObject(3, playerName);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable Statistic selectTop(@NotNull Integer ordinal, @NotNull TopType type) {
        var sql = switch (type) {
            case KICK -> """
                    select * from statistic order by kick_count limit ?, 1
                    """;
            case VOTE -> """
                    select * from statistic order by vote_count limit ?, 1
                    """;
        };

        try(var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, ordinal - 1);
            return mapOne(stm.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void initTables() throws SQLException {
        try (var stm = getConnection().createStatement()) {
            stm.execute("""
                    create table if not exists statistic
                    (
                        player_name text              not null
                            primary key
                            unique,
                        vote_count  integer default 0 not null,
                        kick_count  integer default 0 not null
                    );
                    """);
        }
    }


}
