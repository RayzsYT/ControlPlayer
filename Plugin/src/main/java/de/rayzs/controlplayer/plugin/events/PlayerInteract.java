package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerInteractEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerInteract implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();

        if (instanceState == 0 && event.getAction().toString().contains("LEFT")) swap.checkAndSwap(player);
        if(event.getClickedBlock() == null) return;
        final String typeName = event.getClickedBlock().getType().name();
        if(typeName.contains("ENDER") && typeName.contains("CHEST")) {
            if(instanceState == 0 && !useSwap) {
                event.setCancelled(true);
                Player victim = instance.victim();
                player.openInventory(victim.getEnderChest());
                victim.openInventory(victim.getEnderChest());
                return;
            }
        }

        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
    }
}
