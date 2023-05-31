package io.github.tanyaofei.votekick.properties;

import io.github.tanyaofei.votekick.properties.constant.HK;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Data
@Accessors(chain = true)
public class HelpProperties {

    private Map<HK, String> helps;

    public HelpProperties(FileConfiguration file) {
        this.reload(file);
    }

    public Component get(HK key) {
        var help = helps.get(key);
        return Component.text(Objects.requireNonNullElseGet(help, () -> "Missing help key: " + key));

    }

    public void reload(FileConfiguration file) {
        var helps = new HashMap<HK, String>();
        for (var key : HK.values()) {
            var help = file.getStringList("translate.command-help.vk." + key.name());
            if (help.isEmpty()) {
                continue;
            }

            var text = String.join("\n", help);
            helps.put(key, text);
        }
        this.helps = helps;
    }

}
