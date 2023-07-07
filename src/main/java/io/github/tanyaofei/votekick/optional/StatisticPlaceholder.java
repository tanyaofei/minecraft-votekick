package io.github.tanyaofei.votekick.optional;

import io.github.tanyaofei.votekick.repository.StatisticRepository;
import io.github.tanyaofei.votekick.repository.model.TopType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class StatisticPlaceholder extends PlaceholderExpansion {

    /**
     *
     * top.1.vote.name
     * top.2.vote.num
     * <br>
     * top.1.kick.name
     * top.1.kick.num
     */
    private final static Pattern PATTERN = Pattern.compile(
            "^top\\.(?<ordinal>\\d+)\\.(?<type>vote|kick)\\.(?<data>name|num)"
    );

    private final StatisticRepository repository = StatisticRepository.instance;

    private final static String VERSION = "1";

    @Override
    public @NotNull String getIdentifier() {
        return "votekick";
    }

    @Override
    public @NotNull String getAuthor() {
        return "hello09x";
    }

    @Override
    public @NotNull String getVersion() {
        return VERSION;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        var matcher = PATTERN.matcher(params);
        if (!matcher.find()) {
            return params;
        }

        var ordinal = Integer.parseInt(matcher.group("ordinal"));
        var type = matcher.group("type");
        var data = matcher.group("data");

        var t = switch (type) {
            case "vote" -> TopType.VOTE;
            case "kick" -> TopType.KICK;
            default -> null;
        };

        if (t == null) {
            return params;
        }

        var stat = repository.selectTop(ordinal, t);
        if (stat == null) {
            return "";
        }

        return switch (data) {
            case "name" -> stat.playerName();
            case "num" -> switch (t) {
                case VOTE -> String.valueOf(stat.voteCount());
                case KICK -> String.valueOf(stat.kickCount());
            };
            default -> "";
        };
    }


}
