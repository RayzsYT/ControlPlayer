package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.block.BlockBreakEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if (instanceState == 1) event.setCancelled(true);
    }
}
