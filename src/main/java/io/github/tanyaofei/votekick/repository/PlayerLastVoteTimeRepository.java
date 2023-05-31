package io.github.tanyaofei.votekick.repository;

import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerLastVoteTimeRepository {

    private final ConcurrentMap<String, LocalDateTime> repository = new ConcurrentHashMap<>();

    public static PlayerLastVoteTimeRepository getInstance() {
        return InstanceHolder.instance;
    }

    public LocalDateTime getLastCreateVotingTime(CommandSender sender) {
        return repository.get(sender.getName());
    }

    public void saveOrUpdate(String creator, LocalDateTime time) {
        this.repository.put(creator, time);
    }

    private static final class InstanceHolder {
        private static final PlayerLastVoteTimeRepository instance = new PlayerLastVoteTimeRepository();
    }
}
