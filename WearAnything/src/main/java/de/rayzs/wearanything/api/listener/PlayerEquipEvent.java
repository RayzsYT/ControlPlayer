package de.rayzs.wearanything.api.listener;

import de.rayzs.wearanything.api.WearArmor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerEquipEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private final Player player;
    private final WearArmor wearArmor;
    private final ItemStack item;

    public PlayerEquipEvent(Player player, WearArmor wearArmor, ItemStack item) {
        this.player = player;
        this.wearArmor = wearArmor;
        this.item = item;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public WearArmor getWearArmor() {
        return wearArmor;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
