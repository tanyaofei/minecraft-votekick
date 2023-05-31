package io.github.tanyaofei.votekick.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpAddressUtils {


    public static String getIpAddress(Player player) {
        var address = player.getAddress();
        if (address == null) {
            return "unknown";
        }

        return address.getHostString();
    }

}
