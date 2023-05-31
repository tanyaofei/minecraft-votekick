package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.util.command.ParentCommand;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class VotekickCommand extends ParentCommand {

    public static VotekickCommand getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.help);
    }

    public static class InstanceHolder {

        public static VotekickCommand instance = new VotekickCommand();

        static {
            instance.register("?", new HelpCommand("votekick.help"));
            instance.register("help", "?");
            instance.register("create", new CreateCommand("votekick.create"));
            instance.register("yes", new VoteCommand.YesCommand("votekick.vote"));
            instance.register("no", new VoteCommand.NoCommand("votekick.vote"));
            instance.register("cancel", new CancelCommand("votekick.cancel"));
            instance.register("info", new InfoCommand("votekick.info"));
            instance.register("unkick", new UnkickCommand("votekick.unkick"));
            instance.register("reload", new ReloadCommand("votekick.reload"));
        }
    }


}
