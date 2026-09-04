package de.rayzs.controlplayer.plugin.listener.listeners;

import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class InventoryListener extends ControlPlayerListener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        if( !(event.getWhoClicked() instanceof Player player) || event.isCancelled()) {
            return;
        }

        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        final InventoryView view = event.getView();
        final Inventory topInventory = view.getTopInventory();

        if (topInventory.getType() == InventoryType.WORKBENCH && isControlling(session, player)) {
            schedulerProvider.createScheduler(task -> {
                syncWorkbenchCrafting(session);
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(final InventoryDragEvent event) {
        if( !(event.getWhoClicked() instanceof Player player) || event.isCancelled()) {
            return;
        }

        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }


        final InventoryView view = event.getView();
        final Inventory topInventory = view.getTopInventory();

        if (topInventory.getType() == InventoryType.WORKBENCH && isControlling(session, player)) {
            schedulerProvider.createScheduler(task -> {
                syncWorkbenchCrafting(session);
            });
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if( !(event.getPlayer() instanceof Player player)) {
            return;
        }


        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;



        if (isBeingControlled(session, player)) {
            return;
        }


        final Player controlled = session.getControlledPlayer().get();

        schedulerProvider.createScheduler(task -> {
            final Inventory inventory = event.getInventory();

            if (inventory.getType() == InventoryType.WORKBENCH) {
                controlled.openWorkbench(controlled.getLocation(), true);
                syncWorkbenchCrafting(session);
            } else {
                controlled.openInventory(inventory);
            }
        });
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if( !(event.getPlayer() instanceof Player player)) {
            return;
        }

        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;



        // Controlled player stuck in workbench

        if (isBeingControlled(session, player)) {
            final Player controller = session.getControllingPlayer().get();
            final Inventory controllerOpenInventory = controller.getOpenInventory().getTopInventory();
            final InventoryType controllerOpenInventoryType = controllerOpenInventory.getType();


            if (controllerOpenInventoryType != InventoryType.CRAFTING && controllerOpenInventoryType != InventoryType.CREATIVE) {
                schedulerProvider.createScheduler(task -> {
                    player.openInventory(controllerOpenInventory);
                });
            }

            return;
        }



        // Controlling player closing inventory

        final InventoryType openInventoryType = event.getInventory().getType();
        final Player controlled = session.getControlledPlayer().get();

        if (openInventoryType != InventoryType.CRAFTING && openInventoryType != InventoryType.CREATIVE) {
            schedulerProvider.createScheduler(task -> {
                controlled.closeInventory();
            });
        }
    }


    private void syncWorkbenchCrafting(final Session<Player> session) {
        final Player controller = session.getControllingPlayer().get();
        final Player controlled = session.getControlledPlayer().get();


        if (controller == null || controlled == null) return;

        final InventoryView controllerView = controller.getOpenInventory();
        final InventoryView controlledView = controlled.getOpenInventory();

        final Inventory controllerTop = controllerView.getTopInventory();
        final Inventory controlledTop = controlledView.getTopInventory();


        if (controllerTop instanceof CraftingInventory controllerInv && controlledTop instanceof CraftingInventory controlledInv) {
            controlledInv.setMatrix(controllerInv.getMatrix());
            controlledInv.setResult(controllerInv.getResult());

            controlled.updateInventory();
        }
    }

}
