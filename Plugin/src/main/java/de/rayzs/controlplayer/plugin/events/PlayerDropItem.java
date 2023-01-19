package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlManager;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerDropItem implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        event.setCancelled(true);
    }
}
