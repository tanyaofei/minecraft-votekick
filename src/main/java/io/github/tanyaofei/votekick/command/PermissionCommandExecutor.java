package io.github.tanyaofei.votekick.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PermissionCommandExecutor implements TabExecutor {

    public abstract boolean hasPermission(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    );

    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!hasPermission(sender, command, label, args)) {
            return false;
        }

        return this.onCommand(sender, command, label, args);
    }

    public List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!hasPermission(sender, command, label, args)) {
            return null;
        }
        return this.onTabComplete(sender, command, label, args);
    }

}
