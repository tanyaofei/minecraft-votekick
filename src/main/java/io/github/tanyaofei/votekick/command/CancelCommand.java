package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.manager.VoteManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class CancelCommand extends ExecutableCommand {

    public final static CancelCommand instance = new CancelCommand(
            "终止当前投票",
            "/votekick cancel",
            "votekick.admin.cancel"
    );

    private final VoteManager manager = VoteManager.instance;

    public CancelCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
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
            sender.sendMessage(text("现在没有在投票...", GRAY));
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
