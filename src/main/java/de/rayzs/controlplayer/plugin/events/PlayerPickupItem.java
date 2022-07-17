package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlManager;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerPickupItem implements Listener {

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        event.setCancelled(true);
    }
}
