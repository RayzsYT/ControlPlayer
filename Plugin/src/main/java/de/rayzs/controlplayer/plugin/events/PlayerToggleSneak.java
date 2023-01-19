package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.PlayerToggleSneakEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerToggleSneak implements Listener {

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                victim.setSneaking(event.isSneaking());
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }
}
