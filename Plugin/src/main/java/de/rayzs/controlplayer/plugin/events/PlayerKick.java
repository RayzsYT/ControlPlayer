package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) {
            if(event.getReason().equals("Flying is not enabled on this server"))
                event.setCancelled(true);
        }
    }
}
