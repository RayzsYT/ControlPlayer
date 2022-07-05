package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.message.MessageManager;
import de.rayzs.controlplayer.api.files.message.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath extends MessageManager implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int instanceState = ControlManager.getInstanceState(player);

        switch (instanceState) {
            case 0:
                event.setDeathMessage(null);
            case 1:
                ControlInstance instance = ControlManager.getControlInstance(player);
                Player controller = instance.controller();
                ControlManager.deleteControlInstance(player);
                if (instance.controller() != null) controller.sendMessage(getMessage(MessageType.PLAYER_DIED));
        }
    }
}