package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.VotekickProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;

public class ReloadCommand extends ExecutableCommand {

    public final static ReloadCommand instance = new ReloadCommand("votekick.admin.reload");
    private final VotekickProperties properties = VotekickProperties.instance;

    public ReloadCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return textOfChildren(
                text("重载配置文件", NamedTextColor.GRAY)
        );
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        properties.reload();
        sender.sendMessage(Component.text("[votekick] Success!"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return Collections.emptyList();
    }
}
