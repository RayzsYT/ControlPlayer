package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerInteractEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;
        Player player = event.getPlayer();

        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        event.setCancelled(true);
    }
}
