package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import io.github.tanyaofei.votekick.manager.VoteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class InfoCommand extends ExecutableCommand {

    public final static InfoCommand instance = new InfoCommand("votekick.info");

    private final VoteManager manager = VoteManager.instance;

    public InfoCommand(@Nullable String permission) {
        super(permission);
    }

    private final static Component help = Helps.help(
            "查看投票信息",
            "查看当前的投票信息",
            List.of(
                    new Helps.Content("用法", "/vk info")
            )
    );

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        var vote = manager.getCurrent();
        if (vote == null) {
            sender.sendMessage(text("现在没有在投票中...", GRAY));
            return true;
        }

        var nums = vote.getNums();
        var approvedNums = nums[0];
        var disapproveNums = nums[1];

        sender.sendMessage(textOfChildren(
                text(vote.getCreator(), GOLD),
                text(" 对 "),
                text(vote.getTarget(), GOLD),
                text(" 发起的投票, 原因是: "),
                text(vote.getReason(), Style.style(GRAY, ITALIC)),
                newline(),
                text("目前 ", DARK_GREEN),
                text(approvedNums + " 票赞成, ", DARK_GREEN),
                text(disapproveNums + " 反对", DARK_GREEN)
        ));

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
