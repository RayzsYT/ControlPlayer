package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;
import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreProcess implements Listener {

    private final ControlPlayerPlugin instance;

    public PlayerCommandPreProcess() {
        this.instance = ControlPlayerPlugin.getInstance();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreprocessCommand(PlayerCommandPreprocessEvent event) {
        if(event.isCancelled() || !(boolean) SettingsManager.getSetting(SettingType.CONTROL_RUNNING_CANCELCOMMANDS)) return;

        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState == 1) {
            event.setCancelled(true);
            ControlManager.getControlInstance(player)
                    .controller()
                    .sendMessage(MessageManager.getMessage(MessageType.SPY_CHAT_MESSAGE)
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
        }
    }
}
