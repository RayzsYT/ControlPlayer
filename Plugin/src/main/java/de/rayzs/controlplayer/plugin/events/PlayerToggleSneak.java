package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerToggleSneakEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerToggleSneak implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
        else {
            if(instance.victim() == player) return;

            Player victim = instance.victim();
            victim.setSneaking(event.isSneaking());
        }
    }
}
