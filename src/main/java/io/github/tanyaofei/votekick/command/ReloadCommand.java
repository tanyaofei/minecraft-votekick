package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.util.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends ExecutableCommand {

    public ReloadCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.reload);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        Votekick.getInstance().reload();
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
