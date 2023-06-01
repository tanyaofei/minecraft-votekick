package io.github.tanyaofei.votekick.listener;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.properties.constant.LK;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class VotekickPlayerListener implements Listener {

    private final static Logger log = Votekick.getLog();

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        var kicking = Votekick.getVoteManager().getKicked(event.getPlayer());
        if (kicking != null) {
            var canJoinIn = formatter.format(kicking.getCanJoinIn());

            // op 可以重新加入游戏
            event.disallow(
                    PlayerLoginEvent.Result.KICK_OTHER,
                    Votekick
                            .getConfigManager()
                            .getLanguageProperties()
                            .format(LK.ConnectRejected,
                                    kicking.getReason(),
                                    canJoinIn
                            )
            );

            if (event.getPlayer().isOp()) {
                Votekick.getVoteManager().unkick(event.getPlayer());
                if (Votekick.isDebug()) {
                    log.info(String.format("解除对 OP %s 的封禁", event.getPlayer().getName()));
                }
            }
            log.info(String.format(
                    "拒绝了玩家 %s 加入游戏因为他被投票踢出了服务器，可重新加入的时间: %s",
                    event.getPlayer().getName(), canJoinIn)
            );
        }
    }
}
