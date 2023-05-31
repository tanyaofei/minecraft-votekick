package io.github.tanyaofei.votekick.util.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CoreCommand implements TabExecutor {

    @Nullable
    protected final String permission;

    protected CoreCommand(@Nullable String permission) {
        this.permission = permission;
    }

    protected CoreCommand() {
        this(null);
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    @NotNull
    public abstract Component getHelp();

}
