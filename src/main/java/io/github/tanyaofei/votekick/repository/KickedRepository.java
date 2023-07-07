package io.github.tanyaofei.votekick.repository;

import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.repository.model.Kicked;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class KickedRepository extends AbstractRepository<Kicked> {

    public final static KickedRepository instance = new KickedRepository(Votekick.getInstance());

    public KickedRepository(Plugin plugin) {
        super(plugin);
    }

    public int deleteByPlayerName(@NotNull String playerName) {
        var sql = """
                delete from kicked
                where player_name = ?
                """;

        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, playerName);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull List<Kicked> selectByPlayerName(@NotNull String playerName) {
        var now = Timestamp.valueOf(LocalDateTime.now());
        var sql = """
                select * from kicked
                where player_name = ?
                and started_at <= ? and expired_at > ?
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, playerName);
            stm.setTimestamp(2, now);
            stm.setTimestamp(3, now);
            return mapMany(stm.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Kicked> selectLikePlayerName(@Nullable String playerName) {
        var now = Timestamp.valueOf(LocalDateTime.now());
        var sql = """
                    select * from kicked
                    where player_name like ?
                    and started_at <= ? and expired_at > ?
                    """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, "%" + playerName + "%");
            stm.setTimestamp(2, now);
            stm.setObject(3, now);
            return mapMany(stm.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(@NotNull Kicked kicked) {
        var sql = """
                insert into kicked (
                    player_name,
                    started_at,
                    expired_at
                ) values (
                    ?, ?, ?
                )
                """;

        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, kicked.playerName());
            stm.setTimestamp(2, Timestamp.valueOf(kicked.startedAt()));
            stm.setObject(3, Timestamp.valueOf(kicked.expiredAt()));
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int cleanExpired() {
        var sql = """
                delete from kicked
                where expired_at < ?
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void initTables() throws SQLException {
        try (var stm = getConnection().createStatement()) {
            stm.execute("""
                create table if not exists kicked
                (
                    id          integer  not null
                        primary key autoincrement
                        unique,
                    player_name text     not null,
                    started_at  timestamp not null,
                    expired_at  timestamp not null
                );
                """);
                        stm.execute("""
                create index if not exists kicked_player_name_index
                    on kicked (player_name);
                """);
        }

    }

}
