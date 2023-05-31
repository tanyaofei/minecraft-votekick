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

public class InfoCommand extends ExecutableCommand {

    public InfoCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.info);
    }

    @Override
    public boolean onCommandInternal(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var vote = Votekick.getVoteManager().getCurrent();
        if (vote == null) {
            sender.sendMessage(Votekick.getConfigManager().getLanguageProperties().format(LK.Error_VoteNotFound));
            return true;
        }

        sender.sendMessage(Votekick.getVoteManager().getInfo(vote));
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
