package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.command.PermissionCommandExecutor;
import io.github.tanyaofei.votekick.properties.constant.HK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class HelpCommandExecutor extends PermissionCommandExecutor {

    public static final String permission = "votekick.help";

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
           sender.sendMessage(Votekick.getConfigManager().getHelpProperties().get(HK.help));
           return true;
        }

        return false;
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

    @Override
    public boolean hasPermission(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        return sender.hasPermission(permission);
    }
}
