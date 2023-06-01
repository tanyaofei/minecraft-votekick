package io.github.tanyaofei.votekick;

import io.github.tanyaofei.votekick.command.VotekickCommand;
import io.github.tanyaofei.votekick.listener.VotekickPlayerListener;
import io.github.tanyaofei.votekick.manager.VoteManager;
import io.github.tanyaofei.votekick.properties.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Votekick extends JavaPlugin {
    private static Votekick instance;
    private static ConfigManager configManager;
    private static VoteManager voteManager;

    private static Logger log;

    public static Votekick getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static VoteManager getVoteManager() {
        return voteManager;
    }

    public static Logger getLog() {
        return log;
    }

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();

        {
            configManager = new ConfigManager(this.getConfig());
            voteManager = new VoteManager(configManager.getConfigProperties());
        }

        {
            getServer().getPluginCommand("votekick").setExecutor(VotekickCommand.getInstance());
            getServer().getPluginManager().registerEvents(new VotekickPlayerListener(), this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        reloadConfig();
        configManager.reload(getConfig());
    }

    public static boolean isDebug() {
        return getConfigManager().getConfigProperties().isDebug();
    }

}
