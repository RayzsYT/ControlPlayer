package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerPickupItem implements Listener {

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
    }
}
