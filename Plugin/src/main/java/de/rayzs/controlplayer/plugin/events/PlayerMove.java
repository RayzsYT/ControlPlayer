package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerMove implements Listener {


    // Completely Experimental

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer(), victim, controller;
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        victim = instance.victim();
        controller = instance.controller();

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);

        if(useSwap && instanceState == 0)
            player.teleport(victim.getLocation());
        else if(useSwap && instanceState == 1)
            controller.teleport(player.getLocation());
        else if(!useSwap && instanceState == 0)
            victim.teleport(player.getLocation());
        else if(!useSwap && instanceState == 1)
            player.teleport(controller.getLocation());
    }
}
