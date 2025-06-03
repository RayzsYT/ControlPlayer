package de.rayzs.wearanything.plugin.listener;

import org.bukkit.event.player.PlayerInteractEvent;
import de.rayzs.wearanything.plugin.WearAnything;
import org.bukkit.event.inventory.*;
import de.rayzs.wearanything.api.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.*;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT"))
            return;

        WearAnything api = WearAnything.getInstance();

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Inventory inventory = player.getInventory();
        EquipmentSlot equipmentSlot = event.getHand();

        if (item == null || equipmentSlot == null)
            return;

        WearArmor wearArmor = WearAnything.getInstance().getArmorFromStack(item);

        if (wearArmor == null)
            return;

        ItemStack inventoryArmorSlotItem = inventory.getItem(wearArmor.getArmorType().get());

        if (!api.handleEquip(player, wearArmor, item))
            return;

        inventory.setItem(wearArmor.getArmorType().get(), item);

        if (player.getGameMode() != GameMode.CREATIVE)
            player.getEquipment().setItem(equipmentSlot, inventoryArmorSlotItem);

        if (equipmentSlot == EquipmentSlot.HAND)
            player.swingMainHand();
        else
            player.swingOffHand();

        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        WearAnything api = WearAnything.getInstance();

        Inventory inventory = event.getClickedInventory();
        ClickType type = event.getClick();

        if (inventory == null)
            return;

        if (! (event.getWhoClicked() instanceof Player player))
            return;

        if (inventory.getType() != InventoryType.PLAYER)
            return;

        int slot = event.getSlot();

        if (type.isKeyboardClick())
            return;

        ItemStack clickedItem = inventory.getItem(event.getSlot());

        if (clickedItem == null)
            clickedItem = new ItemStack(Material.AIR);

        if (!api.isValidSlot(slot)) {
            WearArmor wearArmor = WearAnything.getInstance().getArmorFromStack(clickedItem);

            if (type != ClickType.SHIFT_LEFT || wearArmor == null)
                return;

            if (inventory.getItem(wearArmor.getArmorType().get()) != null)
                return;

            if (!api.isValid(clickedItem) || !api.handleEquip(player, wearArmor, clickedItem))
                return;

            inventory.setItem(wearArmor.getArmorType().get(), clickedItem);
            inventory.setItem(slot, new ItemStack(Material.AIR));

            event.setCancelled(true);
            return;
        }

        if (clickedItem.getType() != Material.AIR && type.name().contains("SHIFT_"))
            return;

        ItemStack cursorItem = event.getCursor();
        ArmorEnum clickingSlot = api.getArmorTypeBySlot(slot);

        if (!api.isValid(cursorItem) || clickingSlot == null)
            return;

        WearArmor wearArmor = WearAnything.getInstance().getArmorFromStack(cursorItem);

        if (wearArmor.getArmorType() != clickingSlot || !api.handleEquip(player, wearArmor, cursorItem))
            return;

        inventory.setItem(slot, cursorItem);
        event.setCursor(clickedItem);
        event.setCancelled(true);
    }
}
