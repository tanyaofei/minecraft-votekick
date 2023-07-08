package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VotekickCommand extends ParentCommand {


    public final static VotekickCommand instance = new VotekickCommand(
            "投票踢人相关命令",
            null
    );

    static {
        instance.register("create", CreateCommand.instance);
        instance.register("yes", VoteCommand.YesCommand.instance);
        instance.register("no", VoteCommand.NoCommand.instance);
        instance.register("cancel", CancelCommand.instance);
        instance.register("info", InfoCommand.instance);
        instance.register("unkick", UnkickCommand.instance);
        instance.register("reload", ReloadCommand.instance);
    }

    protected VotekickCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
