package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerToggleFlightEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerToggleFlight implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                victim.setFlying(event.isFlying());
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }
}
