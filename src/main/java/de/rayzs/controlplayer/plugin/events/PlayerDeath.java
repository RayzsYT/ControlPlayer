package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.api.files.message.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath extends MessageManager implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance;

        switch (instanceState) {
            case 0:
                event.setDeathMessage(null);
            case 1:
                instance = ControlManager.getControlInstance(player);
                Player controller = instance.controller();
                ControlManager.deleteControlInstance(player);
                if (instance.controller() != null) controller.sendMessage(getMessage(MessageType.PLAYER_DIED));
        }
    }
}