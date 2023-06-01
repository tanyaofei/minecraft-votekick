package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.util.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class CreateCommand extends ExecutableCommand {

    protected CreateCommand(@Nullable String permission) {
        super(permission);
    }

    private static String getReason(String[] args) {
        if (args.length < 2) {
            return Votekick.getConfigManager().getConfigProperties().getDefaultReason();
        }
        var joiner = new StringJoiner(" ");
        for (int i = 1; i < args.length; i++) {
            joiner.add(args[i]);
        }
        return joiner.toString();
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.create);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length < 1) {
            return false;
        }

        var target = args[0];
        var reason = getReason(args);

        Votekick.getVoteManager().createVote(sender, target, reason);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        var players = Votekick
                .getInstance()
                .getServer()
                .getOnlinePlayers()
                .stream();

        if (!Votekick.getConfigManager().getConfigProperties().isAllowKickOp()) {
            // 不允许踢 OP 则过滤掉 OP 用户
            players = players.filter(player -> !player.isOp());
        }

        var names = players.map(Player::getName);
        if (!args[0].isEmpty()) {
            // 如果内容则过滤内容
            names = names.filter(name -> name.startsWith(args[0]));
        }
        return names.toList();
    }
}
