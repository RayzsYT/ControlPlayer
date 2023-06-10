package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.files.messages.*;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class PlayerChat extends MessageManager implements Listener {

    private final ControlPlayerPlugin instance;

    public PlayerChat() {
        this.instance = ControlPlayerPlugin.getInstance();
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled() || (boolean) SettingsManager.getSetting(SettingType.SYSTEM_ASYNCCHAT)) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        int instanceState = ControlManager.getInstanceState(player);
        Object forceChatObject = SettingsManager.getSetting(SettingType.CONTROL_RUNNING_FORCECHAT_ENABLED);
        boolean forceChat = forceChatObject == null || (boolean) forceChatObject;

        Object bypassMessageObject = SettingsManager.getSetting(SettingType.CONTROL_RUNNING_FORCECHAT_BYPASSMESSAGE);
        String bypassMessage = bypassMessageObject == null ? "-b " : (String) bypassMessageObject;

        if(!forceChat || instanceState != 0) return;

        ControlInstance instance = ControlManager.getControlInstance(player);
        if(message.toLowerCase().startsWith(bypassMessage)) {
            message = message.replace(bypassMessage, "");
            event.setMessage(message);
        } else {
            Player victim = instance.victim();
            event.setCancelled(true);
            victim.chat(event.getMessage());
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled() || !(boolean) SettingsManager.getSetting(SettingType.SYSTEM_ASYNCCHAT)) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        int instanceState = ControlManager.getInstanceState(player);
        Object forceChatObject = SettingsManager.getSetting(SettingType.CONTROL_RUNNING_FORCECHAT_ENABLED);
        boolean forceChat = forceChatObject == null || (boolean) forceChatObject;

        Object bypassMessageObject = SettingsManager.getSetting(SettingType.CONTROL_RUNNING_FORCECHAT_BYPASSMESSAGE);
        String bypassMessage = bypassMessageObject == null ? "-b " : (String) bypassMessageObject;

        if(!forceChat || instanceState != 0) return;

        ControlInstance instance = ControlManager.getControlInstance(player);
        if(message.toLowerCase().startsWith(bypassMessage)) {
            message = message.replace(bypassMessage, "");
            event.setMessage(message);
        } else {
            Player victim = instance.victim();
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(ControlPlayerPlugin.getInstance(), () -> victim.chat(event.getMessage()));
        }
    }
}