package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.api.files.message.*;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class AsyncPlayerChat extends MessageManager implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
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
}