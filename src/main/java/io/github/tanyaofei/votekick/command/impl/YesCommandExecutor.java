package io.github.tanyaofei.votekick.command.impl;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.model.VoteChoice;
import io.github.tanyaofei.votekick.properties.constant.HK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class YesCommandExecutor extends VoteCommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && args[1].equals("?")) {
            sender.sendMessage(Votekick.getConfigManager().getHelpProperties().get(HK.yes));
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    @NotNull
    @Override
    protected VoteChoice choice() {
        return VoteChoice.APPROVE;
    }


}
