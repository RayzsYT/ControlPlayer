package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.block.BlockPlaceEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if (instanceState == 1) event.setCancelled(true);
    }
}
