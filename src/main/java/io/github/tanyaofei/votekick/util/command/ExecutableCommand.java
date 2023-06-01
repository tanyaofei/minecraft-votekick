package io.github.tanyaofei.votekick.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ExecutableCommand extends CoreCommand {

    public ExecutableCommand() {
        super();
    }

    public ExecutableCommand(@Nullable String permission) {
        super(permission);
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

        if (args.length == 1 && args[0].equals("?") || !execute(sender, command, label, args)) {
            sender.sendMessage(this::getHelp);
        }
        return true;
    }

    protected abstract boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    );

}
