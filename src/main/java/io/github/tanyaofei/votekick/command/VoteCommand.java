package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.manager.VoteManager;
import io.github.tanyaofei.votekick.repository.model.VoteChoice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public abstract class VoteCommand extends ExecutableCommand {

    private final VoteManager manager = VoteManager.instance;

    public VoteCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("你不是玩家...", GRAY));
            return true;
        }

        var vote = manager.getCurrent();
        if (vote == null) {
            sender.sendMessage(text("现在没有在投票...", GRAY));
            return true;
        }

        manager.vote(player, vote, this.choice());
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

        public final static YesCommand instance = new YesCommand(
                "赞成投票",
                "/votekick yes",
                "votekick.vote"
        );

        public YesCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
            super(description, usage, permission);
        }

        @Override
        protected @NotNull VoteChoice choice() {
            return VoteChoice.APPROVE;
        }

    }

    public static class NoCommand extends VoteCommand {

        public final static NoCommand instance = new NoCommand(
                "反对投票",
                "/votekick no",
                "votekick.vote"
        );

        public NoCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
            super(description, usage, permission);
        }


        @Override
        protected @NotNull VoteChoice choice() {
            return VoteChoice.DISAPPROVE;
        }

    }
}


