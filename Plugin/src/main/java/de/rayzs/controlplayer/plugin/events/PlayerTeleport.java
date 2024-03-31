package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;

public class PlayerTeleport implements Listener {

    private final ControlPlayerPlugin instance;

    public PlayerTeleport() {
        this.instance = ControlPlayerPlugin.getInstance();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player victim = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(victim);

        if(instance == null || event.getTo() == null) return;

        Location toLoc = event.getTo(), fromLoc = event.getFrom();
        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(victim);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if(event.getTo() == null || useSwap || instanceState == 0) return;

        if(!Arrays.asList(PlayerTeleportEvent.TeleportCause.COMMAND, PlayerTeleportEvent.TeleportCause.PLUGIN).contains(event.getCause())) return;
        Player controller = instance.controller();

        if(event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            if(fromLoc.distance(toLoc) < 5) return;
            controller.teleport(victim.getLocation());
            event.setCancelled(true);
            return;
        }

        controller.teleport(victim.getLocation());
        event.setCancelled(true);
    }
}
