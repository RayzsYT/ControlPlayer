package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class InventoryInteraction implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player) || event.isCancelled()) return;

        Player player = (Player) event.getWhoClicked();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player) || event.isCancelled()) return;

        Player player = (Player) event.getWhoClicked();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
    }
}
