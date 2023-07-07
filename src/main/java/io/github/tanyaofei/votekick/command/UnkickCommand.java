package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import io.github.tanyaofei.votekick.repository.model.Kicked;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;

public class UnkickCommand extends ExecutableCommand {

    public final static UnkickCommand instance = new UnkickCommand("votekick.admin.*");

    private final KickedRepository kickedRepository = KickedRepository.instance;

    public UnkickCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return textOfChildren(
                text("取消踢出玩家\n", NamedTextColor.GRAY),
                text("用法: /vk unkick <玩家>", NamedTextColor.WHITE)
        );
    }


    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return false;
        }

        var success = kickedRepository.deleteByPlayerName(args[0]) > 0;
        if (success) {
            sender.sendMessage(Component.text("解除踢出成功", NamedTextColor.GRAY));
        } else {
            sender.sendMessage(Component.text("这个玩家没有被踢出...", NamedTextColor.GRAY));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length < 1) {
            return null;
        }

        if (args[0].isBlank()) {
            return null;
        }

        return kickedRepository
                .selectLikePlayerName(args[0])
                .stream()
                .map(Kicked::playerName)
                .distinct()
                .collect(Collectors.toList());
    }
}
