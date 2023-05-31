package io.github.tanyaofei.votekick.listener;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.LK;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class VotekickPlayerListener implements Listener {

    private final static @NotNull Logger log = Votekick.getInstance().getLogger();

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        var kicking = Votekick.getVoteManager().getKicked(event.getPlayer());
        if (kicking != null) {
            // op 可以重新加入游戏
            event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Votekick
                            .getConfigManager()
                            .getLanguageProperties()
                            .format(LK.ConnectRejected,
                                    kicking.getReason(),
                                    formatter.format(kicking.getCanJoinIn())
                            )
            );

            if (event.getPlayer().isOp()) {
                Votekick.getVoteManager().unkick(event.getPlayer());
            }
            log.info(String.format(
                    "Player %s was refused to connect cause they were kicked by vote",
                    event.getPlayer().getName())
            );
        }
    }
}
