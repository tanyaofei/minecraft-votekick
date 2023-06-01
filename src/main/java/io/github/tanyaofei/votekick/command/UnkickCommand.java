package io.github.tanyaofei.votekick.command;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.model.Kicked;
import io.github.tanyaofei.votekick.properties.constant.HK;
import io.github.tanyaofei.votekick.properties.constant.LK;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import io.github.tanyaofei.votekick.util.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnkickCommand extends ExecutableCommand {

    public UnkickCommand(@Nullable String permission) {
        super(permission);
    }

    @Override
    public @NotNull Component getHelp() {
        return Votekick.getConfigManager().getHelpProperties().get(HK.unkick);
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
        var success = KickedRepository.getInstance().removeByPlayerName(args[0]);
        if (success) {
            sender.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Unkick, args[0])
            );
        } else {
            sender.sendMessage(
                    Votekick.getConfigManager()
                            .getLanguageProperties()
                            .format(LK.Error_KickNotFound)
            );
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

        if (args[0].isEmpty()) {
            return KickedRepository
                    .getInstance()
                    .list()
                    .stream()
                    .map(Kicked::getPlayerName)
                    .toList();
        }

        return KickedRepository
                .getInstance()
                .list()
                .stream()
                .map(Kicked::getPlayerName)
                .filter(name -> name.startsWith(args[1]))
                .toList();
    }
}
