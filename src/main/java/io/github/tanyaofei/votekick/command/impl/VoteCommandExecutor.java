package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.command.PermissionCommandExecutor;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.model.VoteChoice;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class VoteCommandExecutor extends PermissionCommandExecutor {

    private final static String permission = "votekick.vote";

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return sender.hasPermission(permission);
    }

    @NotNull
    protected abstract VoteChoice choice();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return false;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("[votekick] You are not a player..."));
            return true;
        }

        var mgr = Votekick
                .getVoteManager();

        var vote = mgr.getCurrent();
        if (vote == null) {
            sender.sendMessage(Votekick
                    .getConfigManager()
                    .getLanguageProperties()
                    .format(LK.Error_VoteNotFound)
            );
            return true;
        }

        mgr.vote(
                p,
                mgr.getCurrent(),
                this.choice()
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
