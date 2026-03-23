package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

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

        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory != null && topInventory.getType() == InventoryType.WORKBENCH) {
            Player controller = useSwap ? instance.victim() : instance.controller();
            Player victim = instance.victim();

            if (player == controller || player == victim) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), () ->
                        syncWorkbenchCrafting(controller, victim)
                );
            }
        }
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

        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory != null && topInventory.getType() == InventoryType.WORKBENCH) {
            Player controller = useSwap ? instance.victim() : instance.controller();
            Player victim = instance.victim();

            if (player == controller || player == victim) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), () ->
                        syncWorkbenchCrafting(controller, victim)
                );
            }
        }
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
                Inventory inventory = event.getInventory();

                if (inventory != null && inventory.getType() == InventoryType.WORKBENCH) {
                    victim.openWorkbench(victim.getLocation(), true);
                    syncWorkbenchCrafting(player, victim);
                } else {
                    victim.openInventory(inventory);
                }
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

    private void syncWorkbenchCrafting(Player controller, Player victim) {
        if (controller == null || victim == null) return;

        InventoryView controllerView = controller.getOpenInventory();
        InventoryView victimView = victim.getOpenInventory();

        if (controllerView == null || victimView == null) return;

        Inventory controllerTop = controllerView.getTopInventory();
        Inventory victimTop = victimView.getTopInventory();

        if (!(controllerTop instanceof CraftingInventory) || !(victimTop instanceof CraftingInventory)) return;
        if (controllerTop.getType() != InventoryType.WORKBENCH || victimTop.getType() != InventoryType.WORKBENCH) return;

        CraftingInventory controllerInv = (CraftingInventory) controllerTop;
        CraftingInventory victimInv = (CraftingInventory) victimTop;

        victimInv.setMatrix(controllerInv.getMatrix());
        victimInv.setResult(controllerInv.getResult());

        victim.updateInventory();
    }
}
