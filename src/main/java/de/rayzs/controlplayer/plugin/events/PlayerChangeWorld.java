package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorld implements Listener {

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        ControlManager.hideAllControllers(player);
    }
}