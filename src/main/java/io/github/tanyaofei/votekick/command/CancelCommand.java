package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.manager.VoteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CancelCommand extends ExecutableCommand {

    public final static CancelCommand instance = new CancelCommand("votekick.admin.*");
    private final VoteManager manager = VoteManager.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("votekick.admin.*");
    }

    public CancelCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.text("取消投票", NamedTextColor.GRAY);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 0) {
            return false;
        }

        var vote = manager.getCurrent();
        if (vote == null) {
            sender.sendMessage(Component.text("现在没有在投票...", NamedTextColor.GRAY));
            return true;
        }

        manager.cancel(sender, vote);
        return true;
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
}
