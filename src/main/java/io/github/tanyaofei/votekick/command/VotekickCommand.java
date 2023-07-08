package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class VotekickCommand extends ParentCommand {


    public final static VotekickCommand instance = new VotekickCommand();

    private final static Component help = Helps.help(
            "投票踢人",
            "输入 /vk <命令> ? 获得更详细的帮助",
            List.of(
                    new Helps.Content("create <玩家> [理由]", "发起投票"),
                    new Helps.Content("yes", "赞成投票"),
                    new Helps.Content("no", "反对投票"),
                    new Helps.Content("info", "查看当前投票信息"),
                    new Helps.Content("cancel", "取消当前投票"),
                    new Helps.Content("unkick <玩家>", "解除对玩家的踢出"),
                    new Helps.Content("reload", "重载配置文件")
            )
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

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

}
