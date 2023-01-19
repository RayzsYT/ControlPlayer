package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerInteractAtEntity implements Listener {

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if (instanceState != 1) return;
        event.setCancelled(true);
    }
}
