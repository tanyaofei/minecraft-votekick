package io.github.tanyaofei.votekick.properties;

import io.github.tanyaofei.votekick.Votekick;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class ConfigManager {

    @Getter
    private final ConfigProperties configProperties;
    @Getter
    private final LanguageProperties languageProperties;
    @Getter
    private final HelpProperties helpProperties;

    public ConfigManager(FileConfiguration file) {
        getOrCreateFile("config.yml");
        this.configProperties = new ConfigProperties(file);
        this.languageProperties = new LanguageProperties(file);
        this.helpProperties = new HelpProperties(file);
    }

    private static void getOrCreateFile(String filename) {
        var dataFolder = Path.of(Bukkit.getPluginsFolder().getAbsolutePath(), "votekick").toFile();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Could not create data file for Votekick");
        }

        var file = Path.of(dataFolder.getAbsolutePath(), filename).toFile();
        if (!file.exists()) {
            try (var in = Votekick.class.getClassLoader().getResource(filename).openStream()) {
                IOUtil.copy(in, new FileWriter(file));
                Votekick.getInstance().getLogger().info("Default config.yml created");
            } catch (IOException e) {
                throw new UncheckedIOException("Could create default config file", e);
            }
        }
    }

    public void reload(FileConfiguration file) {
        configProperties.reload(file);
        languageProperties.reload(file);
        helpProperties.reload(file);
    }


}
