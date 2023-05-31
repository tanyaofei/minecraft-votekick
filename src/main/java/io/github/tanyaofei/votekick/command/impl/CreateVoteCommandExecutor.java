package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.command.PermissionCommandExecutor;
import io.github.tanyaofei.votekick.properties.constant.HK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class CreateVoteCommandExecutor extends PermissionCommandExecutor {

    private final static String permission = "votekick.create";

    private static String getReason(String[] args) {
        if (args.length < 3) {
            return "-";
        }
        var joiner = new StringJoiner(" ");
        for (int i = 2; i < args.length; i++) {
            joiner.add(args[i]);
        }
        return joiner.toString();
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && args[1].equals("?")) {
            sender.sendMessage(Votekick.getConfigManager().getHelpProperties().get(HK.create));
            return true;
        }

        if (args.length < 2) {
            return false;
        }

        var target = args[1];
        var reason = getReason(args);

        Votekick
                .getVoteManager()
                .createVote(sender, target, reason);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        var players = Votekick
                .getInstance()
                .getServer()
                .getOnlinePlayers()
                .stream();

        if (!Votekick.getConfigManager().getConfigProperties().isAllowKickOp()) {
            players = players.filter(player -> !player.isOp());
        }

        var names = players.map(Player::getName);
        if (!args[1].isEmpty()) {
            names = names.filter(name -> name.startsWith(args[1]));
        }

        return names.toList();
    }
}
