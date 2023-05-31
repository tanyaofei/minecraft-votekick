package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.command.impl.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager implements TabExecutor {

    private final Map<String, PermissionCommandExecutor> commands = new HashMap<>();

    public static CommandManager getInstance() {
        return InstanceHolder.instance;
    }

    public CommandManager register(String command, PermissionCommandExecutor executor) {
        this.commands.put(command, executor);
        return this;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            return false;
        }

        var subcommand = commands.get(args[0]);
        if (subcommand != null) {
            return subcommand.execute(sender, command, label, args);
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
        // 没有参数, 返回所有子命令
        if (args.length == 0) {
            return commands
                    .entrySet()
                    .stream()
                    .filter(c -> c.getValue().hasPermission(sender, command, label, args))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        // 如果第一个参数是一个子命令, 则返回子命令的帮助信息
        var subcommand = commands.get(args[0]);
        if (subcommand != null) {
            return subcommand.tabComplete(sender, command, label, args);
        }

        // 如果第一个参数不是子命令，则返回所有以该参数开头的子命令
        return commands
                .entrySet()
                .stream()
                .filter(c -> c.getValue().hasPermission(sender, command, label, args))
                .filter(c -> c.getKey().startsWith(args[0]))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static class InstanceHolder {
        public final static CommandManager instance = new CommandManager();

        static {
            instance.register("?", new HelpCommandExecutor())
                    .register("help", new HelpCommandExecutor())
                    .register("create", new CreateVoteCommandExecutor())
                    .register("cancel", new CancelVoteCommandExecutor())
                    .register("yes", new YesCommandExecutor())
                    .register("no", new NoCommandExecutor())
                    .register("info", new InfoCommandExecutor())
                    .register("reload", new ReloadCommandExecutor())
                    .register("unkick", new UnkickCommandExecutor())
            ;
        }
    }
}
