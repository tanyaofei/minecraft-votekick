package io.github.tanyaofei.votekick.repository;

import org.bukkit.Server;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerLastVoteTimeRepository {


    private final ConcurrentMap<String, LocalDateTime> repository = new ConcurrentHashMap<>();

    public static ServerLastVoteTimeRepository getInstance() {
        return InstanceHolder.instance;
    }

    public LocalDateTime getLastVotingTime(Server server) {
        return repository.get(server.getName());
    }

    public void saveOrUpdate(Server server, LocalDateTime time) {
        repository.put(server.getName(), time);
    }

    private static class InstanceHolder {
        private final static ServerLastVoteTimeRepository instance = new ServerLastVoteTimeRepository();
    }

}
