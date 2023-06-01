package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.model.VoteChoice;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.util.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class VoteCommand extends ExecutableCommand {

    public VoteCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("[votekick] You are not a player..."));
            return true;
        }

        var mgr = Votekick
                .getVoteManager();

        var vote = mgr.getCurrent();
        if (vote == null) {
            sender.sendMessage(Votekick
                                       .getConfigManager()
                                       .getLanguageProperties()
                                       .format(LK.Error_VoteNotFound)
            );
            return true;
        }

        mgr.vote(
                p,
                mgr.getCurrent(),
                this.choice()
        );
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

    @NotNull
    protected abstract VoteChoice choice();


    public static class YesCommand extends VoteCommand {


        public YesCommand(@Nullable String permission) {
            super(permission);
        }

        @Override
        protected @NotNull VoteChoice choice() {
            return VoteChoice.APPROVE;
        }

        @Override
        public @NotNull Component getHelp() {
            return Votekick.getConfigManager().getHelpProperties().get(HK.yes);
        }
    }

    public static class NoCommand extends VoteCommand {

        public NoCommand(@Nullable String permission) {
            super(permission);
        }

        @Override
        protected @NotNull VoteChoice choice() {
            return VoteChoice.DISAPPROVE;
        }

        @Override
        public @NotNull Component getHelp() {
            return Votekick.getConfigManager().getHelpProperties().get(HK.no);
        }
    }
}
