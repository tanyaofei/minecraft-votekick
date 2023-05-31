package io.github.tanyaofei.votekick.properties;

import io.github.tanyaofei.votekick.properties.constant.LK;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.*;

@Data
@Accessors(chain = true)
public class LanguageProperties {

    private Map<LK, MessageFormat> templates;

    public LanguageProperties(FileConfiguration file) {
        this.reload(file);
    }

    public void reload(FileConfiguration file) {
        var templates = new HashMap<LK, MessageFormat>(LK.values().length);
        for (var key : LK.values()) {
            var format = file.getString("translate.language." + key.name());
            if (format == null) {
                continue;
            }

            templates.put(key, new MessageFormat(format));
        }
        this.templates = templates;
    }

    public Component format(LK key, Object... args) {
        var template = this.templates.get(key);
        if (template == null) {
            return Component.text("<missing language key: " + key + ">");
        }
        return Component.text(template.format(args));
    }

}
