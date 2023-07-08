package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.manager.VoteManager;
import io.github.tanyaofei.votekick.properties.VotekickProperties;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class CreateCommand extends ExecutableCommand {

    public final static CreateCommand instance = new CreateCommand(
            "发起投票",
            "/votekick create <玩家> [原因]",
            "votekick.create"
    );

    private final Votekick votekick = Votekick.getInstance();
    private final VoteManager manager = VoteManager.instance;
    private final VotekickProperties properties = VotekickProperties.instance;

    public CreateCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    private @Nullable String getReason(String[] args) {
        if (args.length < 2) {
            return null;
        }
        return String.join(" ", Arrays.copyOfRange(args, 1, args.length));
    }

    private final static Component help = Helps.help(
            "发起踢人投票",
                    new Helps.Content("用法", "/vk create <玩家> [原因]"),
                    new Helps.Content("例子", "/vk create hello09x 他坏坏")
    );

    @Override
    public @NotNull Component getHelp(int page) {
        return help;
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

        if (reason != null && reason.length() > 256) {
            sender.sendMessage(text("你的理由也太长了吧...", GRAY));
            return true;
        }

        manager.createVote(sender, target, reason);
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

        var players = votekick
                .getServer()
                .getOnlinePlayers()
                .stream();

        if (!properties.isAllowKickOp()) {
            // 不允许踢 OP 则过滤掉 OP 用户
            players = players.filter(player -> !player.isOp());
        }

        var names = players.map(Player::getName);
        if (!args[0].isEmpty()) {
            // 如果内容则过滤内容
            names = names.filter(name -> name.toLowerCase().contains(args[0].toLowerCase()));
        }
        return names.toList();
    }
}
