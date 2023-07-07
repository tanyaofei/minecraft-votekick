package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class VotekickCommand extends ParentCommand {


    public final static VotekickCommand instance = new VotekickCommand();

    static {
        instance.register("create", CreateCommand.instance);
        instance.register("yes", VoteCommand.YesCommand.instance);
        instance.register("no", VoteCommand.NoCommand.instance);
        instance.register("cancel", CancelCommand.instance);
        instance.register("info", InfoCommand.instance);
        instance.register("unkick", UnkickCommand.instance);
        instance.register("reload", ReloadCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("投票踢人命令\n", GOLD),
                Component.text("create", GOLD), Component.text(" - "), Component.text("发起投票\n", DARK_GREEN),
                Component.text("yes", GOLD), Component.text(" - ", GRAY), Component.text("赞成投票\n", DARK_GREEN),
                Component.text("no", GOLD), Component.text(" - ", GRAY), Component.text("反对投票\n", DARK_GREEN),
                Component.text("info", GOLD), Component.text(" - ", GRAY), Component.text("获取投票信息\n", DARK_GREEN),
                Component.text("cancel", GOLD), Component.text(" - ", GRAY), Component.text("取消投票\n", DARK_GREEN),
                Component.text("unkick", GOLD), Component.text(" - ", GRAY), Component.text("取消踢出\n", DARK_GREEN),
                Component.text("reload", GOLD), Component.text(" - ", GRAY), Component.text("重载配置文件", DARK_GREEN)
        );
    }

}
