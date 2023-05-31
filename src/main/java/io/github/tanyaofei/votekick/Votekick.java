package io.github.tanyaofei.votekick;

import io.github.tanyaofei.votekick.command.CommandManager;
import io.github.tanyaofei.votekick.listener.VotekickPlayerListener;
import io.github.tanyaofei.votekick.manager.VoteManager;
import io.github.tanyaofei.votekick.properties.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Votekick extends JavaPlugin {
    private static Votekick instance;
    private static ConfigManager configManager;
    private static VoteManager voteManager;

    public static Votekick getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static VoteManager getVoteManager() {
        return voteManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginCommand("votekick").setExecutor(CommandManager.getInstance());

        configManager = new ConfigManager(this.getConfig());
        voteManager = new VoteManager(configManager.getConfigProperties());

        registerEvent:
        {
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

}
