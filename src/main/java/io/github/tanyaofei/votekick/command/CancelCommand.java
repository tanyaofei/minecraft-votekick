package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.util.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CancelCommand extends ExecutableCommand {

    public CancelCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.cancel);
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

        var mgr = Votekick.getVoteManager();
        var vote = mgr.getCurrent();
        if (vote == null) {
            sender.sendMessage(Votekick.getConfigManager()
                                       .getLanguageProperties()
                                       .format(LK.Error_VoteNotFound)
            );
            return true;
        }

        mgr.cancel(sender, vote);
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
