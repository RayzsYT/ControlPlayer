package de.rayzs.controlplayer.api.specific;

import de.rayzs.controlplayer.api.adapter.LuckPermsAdapter;
import org.bukkit.entity.Player;

public class SpecificControlManager {

    private static boolean initialized = false;

    public static void initialize() {
        initialized = true;
    }

    public static boolean doesPlayerHaveSpecificControlPerms(final Player player) {
        if (!initialized || player.isOp() || player.hasPermission("controlplayer.use"))
            return false;

        return !LuckPermsAdapter.getSpecificPerms(player).isEmpty();
    }

    public static boolean canPlayerControl(final Player controller, final Player target) {
        if (!doesPlayerHaveSpecificControlPerms(controller))
            return false;

        return LuckPermsAdapter.getSpecificPerms(controller).stream().anyMatch(perm -> perm.equalsIgnoreCase("controlplayer.specific." + target.getName()) || perm.equalsIgnoreCase(target.getUniqueId().toString()));
    }
}
