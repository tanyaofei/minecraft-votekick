package io.github.tanyaofei.votekick;

import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import io.github.tanyaofei.votekick.command.VotekickCommand;
import io.github.tanyaofei.votekick.listener.VotekickPlayerListener;
import io.github.tanyaofei.votekick.optional.StatisticPlaceholder;
import io.github.tanyaofei.votekick.properties.VotekickProperties;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Votekick extends JavaPlugin {

    private static Votekick instance;
    private static Logger log;

    @Getter
    private VotekickProperties properties;

    public static Votekick getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();
        this.properties = VotekickProperties.instance;

        {
            getServer().getPluginCommand("votekick").setExecutor(VotekickCommand.instance);
            getServer().getPluginManager().registerEvents(new VotekickPlayerListener(), this);
        }

        supportPlaceHolderAPI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        AbstractRepository.closeConnection(this);
    }

    public void supportPlaceHolderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        if (!new StatisticPlaceholder().register()) {
            log.warning("支援 PlaceholderAPI 失败");
            return;
        }

        log.info("已支援 PlaceholderAPI");
    }

}
