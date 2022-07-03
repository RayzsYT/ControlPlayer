package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.message.*;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin extends MessageManager implements Listener {

    private final ControlPlayerPlugin instance;
    private final boolean latestVersion;

    public PlayerJoin() {
        this.instance = ControlPlayerPlugin.getInstance();
        this.latestVersion = instance.isLatestVersion();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ControlManager.hideAllControllers(player);
        if(!(player.isOp() || player.hasPermission("controlplayer.use")) || latestVersion) return;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if(!player.isOnline()) return;
            player.sendMessage(getMessage(MessageType.PREFIX) + " §cYou're using an outdated version of this plugin!");
            player.sendMessage(getMessage(MessageType.PREFIX) + " §cPlease update it here: §e§o§nhttps://www.rayzs.de/products/controlplayer/page");
        }, 30);
    }
}