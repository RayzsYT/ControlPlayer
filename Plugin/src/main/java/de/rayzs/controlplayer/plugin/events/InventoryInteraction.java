package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.Bukkit;
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

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1)
            return;

        Player victim = instance.victim();

        if(player != victim) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), () -> {
                victim.openInventory(event.getInventory());
            });
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) {
            String openInventoryType = event.getInventory().getType().name();
            if (!openInventoryType.contains("CRAFTING") && !openInventoryType.contains("CREATIVE")) {

                Player controller = useSwap ? instance.victim() : instance.controller();
                String controllerOpenInventory = controller.getOpenInventory().getType().name();

                if (!controllerOpenInventory.contains("CRAFTING") && !controllerOpenInventory.contains("CREATIVE")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), () -> {
                        player.teleport(controller);
                        player.openInventory(event.getInventory());
                    });
                }
            }

            return;
        }

        Player victim = instance.victim();

        if(player != victim) {
            String openInventoryType = event.getInventory().getType().name();
            if (!openInventoryType.contains("CRAFTING") && !openInventoryType.contains("CREATIVE")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), victim::closeInventory);
            }
        }

    }
}
