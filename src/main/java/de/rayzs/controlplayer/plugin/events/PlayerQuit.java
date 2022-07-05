package de.rayzs.controlplayer.plugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.api.files.message.*;
import org.bukkit.event.*;

public class PlayerQuit extends MessageManager implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);

        switch (instanceState) {
            case 0: ControlManager.deleteControlInstance(player); break;
            case 1:
                ControlInstance instance = ControlManager.getControlInstance(player);
                Player controller = instance.controller();
                ControlManager.deleteControlInstance(player);
                if(instance.controller() != null) controller.sendMessage(getMessage(MessageType.PLAYER_LEFT));
                break;
        }
    }
}