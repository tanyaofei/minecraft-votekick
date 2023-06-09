package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.manager.VoteManager;
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

    public final static InfoCommand instance = new InfoCommand(
            "查看当前投票",
            "/votekick info",
            "votekick.info"
    );

    private final VoteManager manager = VoteManager.instance;

    public InfoCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
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
