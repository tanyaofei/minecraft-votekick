package io.github.tanyaofei.votekick.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParentCommand extends CoreCommand {

    @NotNull
    protected Map<String, CoreCommand> subCommands = new HashMap<>();

    public void register(@NotNull String name, @NotNull ExecutableCommand subcommand) {
        this.subCommands.put(name, subcommand);
    }

    public void register(@NotNull String name, @NotNull String from) {
        var subcommand = subCommands.get(from);
        if (subcommand == null) {
            throw new IllegalArgumentException("have not a sub command named: " + from);
        }
        this.subCommands.put(name, subcommand);
    }

    @Override
    public final @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            org.bukkit.command.@NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!hasPermission(sender)) {
            return null;
        }

        // 没有参数或者第一个参数是空，则返回所有有权限执行的子命令
        if (args.length == 0 || args[0].isEmpty()) {
            return subCommands
                    .entrySet()
                    .stream()
                    .filter(cmd -> cmd.getValue().hasPermission(sender))
                    .map(Map.Entry::getKey)
                    .toList();
        }


        // 如果第一个参数是子命令，则递归调用
        var subcommand = subCommands.get(args[0]);
        if (subcommand != null) {
            return subcommand.onTabComplete(
                    sender,
                    command,
                    label,
                    args.length > 1
                            ? Arrays.copyOfRange(args, 1, args.length)
                            : new String[0]
            );
        }

        // 返回所有以当前输入开头的子命令
        return subCommands
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(args[0]) && entry.getValue().hasPermission(sender))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!hasPermission(sender)) {
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(getHelp());
            return true;
        }

        var subcommand = subCommands.get(args[0]);
        if (subcommand == null) {
            sender.sendMessage(getHelp());
            return true;
        }

        return subcommand.onCommand(
                sender,
                command,
                label,
                args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]
        );
    }
}
