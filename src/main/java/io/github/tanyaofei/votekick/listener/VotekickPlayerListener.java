package io.github.tanyaofei.votekick.listener;

import io.github.tanyaofei.votekick.Votekick;
import io.github.tanyaofei.votekick.manager.VoteManager;
import io.github.tanyaofei.votekick.properties.VotekickProperties;
import io.github.tanyaofei.votekick.repository.KickedRepository;
import io.github.tanyaofei.votekick.repository.model.Kicked;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class VotekickPlayerListener implements Listener {

    private final static Logger log = Votekick.getInstance().getLogger();
    private final KickedRepository repository = KickedRepository.instance;
    private final VotekickProperties properties = Votekick.getInstance().getProperties();
    private final VoteManager manager = VoteManager.instance;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        var playerName = event.getPlayer().getName();
        var kicked = repository.selectByPlayerName(playerName);

        if (kicked.isEmpty()) {
            return;
        }

        var expiredAt = kicked
                .stream()
                .map(Kicked::expiredAt)
                .reduce(LocalDateTime.now(), (a, b) -> a.isAfter(b) ? a : b);

        event.disallow(
                PlayerLoginEvent.Result.KICK_OTHER,
                textOfChildren(
                        text("你被踢出了服务器\n", RED),
                        text(String.format("在 %s 之后你可以重新加入\n", expiredAt.format(formatter)), RED),
                        text(":(", RED)
                )
        );

        if (event.getPlayer().isOp() && properties.isAllowOpRejoin()) {
            repository.deleteByPlayerName(playerName);
            log.info(String.format("解除了对 op %s 的踢出", playerName));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var vote = manager.getCurrent();
        if (vote == null) {
            return;
        }

        var player = event.getPlayer();
        vote.getProgress().getBossBar().addPlayer(player);
        player.sendMessage(textOfChildren(
                text("当前正在投票踢 ", RED),
                text(vote.getTarget().equals(player.getName()) ? "你" : vote.getTarget(), NamedTextColor.GOLD),
                text(" 原因是: ", RED),
                text(vote.getReason(), RED)
        ));
    }

}
