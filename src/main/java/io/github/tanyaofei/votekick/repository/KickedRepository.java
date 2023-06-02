package io.github.tanyaofei.votekick.repository;

import io.github.tanyaofei.votekick.model.Kicked;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KickedRepository {

    private final ConcurrentMap<String, Kicked> storage = new ConcurrentHashMap<>(32);

    public Kicked getByPlayer(Player player) {
        return getByPlayerName(player.getName());
    }

    public boolean removeByPlayer(Player player) {
        return removeByPlayerName(player.getName());
    }

    public boolean removeByPlayerName(String playerName) {
        return this.storage.remove(playerName) != null;
    }

    public List<Kicked> list() {
        var now = LocalDateTime.now();
        return this.storage.values().stream().filter(k -> k.getExpires().isAfter(now)).toList();
    }

    public Kicked getByPlayerName(String playerName) {
        var kicked = storage.get(playerName);
        if (kicked != null && kicked.getExpires().isBefore(LocalDateTime.now())) {
            storage.remove(playerName, kicked);
            return null;
        }
        return kicked;
    }

    public void saveOrUpdate(Kicked kicked) {
        storage.put(kicked.getPlayerName(), kicked);
    }

    private static class InstanceHolder {
        private final static KickedRepository instance = new KickedRepository();
    }

    public static KickedRepository getInstance() {
        return InstanceHolder.instance;
    }
}
