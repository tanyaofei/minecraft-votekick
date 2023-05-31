package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.command.PermissionCommandExecutor;
import io.github.tanyaofei.votekick.model.Kicked;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class UnkickCommandExecutor extends PermissionCommandExecutor {

    private final static String permission = "votekick.unkick";

    @Override
    public boolean hasPermission(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
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
            sender.sendMessage(Votekick.getConfigManager().getHelpProperties().get(HK.unkick));
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        var success = KickedRepository.getInstance().removeByPlayerName(args[1]);
        if (success) {
            sender.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_VoteNotFound)
            );
        } else {
            sender.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Unkick, args[1])
            );
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        // vk unkick <name>
        if (args.length != 2) {
            return Collections.emptyList();
        }

        if (args[1].isEmpty()) {
            return KickedRepository
                    .getInstance()
                    .list()
                    .stream()
                    .map(Kicked::getPlayerName)
                    .toList();
        }

        return KickedRepository
                .getInstance()
                .list()
                .stream()
                .map(Kicked::getPlayerName)
                .filter(name -> name.startsWith(args[1]))
                .toList();
    }
}
