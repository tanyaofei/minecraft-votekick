package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.command.PermissionCommandExecutor;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.properties.constant.LK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CancelVoteCommandExecutor extends PermissionCommandExecutor {

    private final static String permission = "votekick.cancel";

    @Override
    public boolean hasPermission(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 2 && args[1].equals("?")) {
            sender.sendMessage(Votekick.getConfigManager().getHelpProperties().get(HK.cancel));
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        var mgr = Votekick.getVoteManager();
        var vote = mgr.getCurrent();
        if (vote == null) {
            sender.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_VoteNotFound)
            );
            return true;
        }

        mgr.cancel(sender, vote);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
