package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerDropItem implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
    }
}
