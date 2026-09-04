package de.rayzs.controlplayer.plugin.listener.listeners;

import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.utils.ExpireCache;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.TimeUnit;

public class ChatListener extends ControlPlayerListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreprocessCommand(final PlayerCommandPreprocessEvent event) {
        if (event.isCancelled() || ConfigData.Setting.VICTIM_CAN_USE_COMMANDS.getValue(Boolean.class)) {
            return;
        }

        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (!isBeingControlled(session, player)) return;

        event.setCancelled(true);

        ConfigData.Message.SPY_CHAT_NOTIFICATION.send(session.getSessionHolder(),
                "%player%", player.getName(), "%message%", event.getMessage()
        );
    }


    private final ExpireCache<String, Long> queuedMessages = new ExpireCache<>(2, TimeUnit.SECONDS);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());

        if (session == null || session.isStopped()) return;


        final String message = event.getMessage();
        final String bypassString = ConfigData.Setting.BYPASS_PREFIX.getValue(String.class);

        final boolean isSessionTarget = isSessionTarget(session, player);


        if (isSessionTarget(session, player)) {
            final long messageTime = queuedMessages.getOrDefault(player.getUniqueId() + "==" + message, -500L);
            final boolean inTime = messageTime != -500 && System.currentTimeMillis() - messageTime <= 1000;

            if (inTime) {
                return;
            }

            if (!ConfigData.Setting.VICTIM_CAN_USE_CHAT.getValue(Boolean.class)) {

                if (ConfigData.Setting.SPY_ON_VICTIM_CHAT.getValue(Boolean.class)) {
                    ConfigData.Message.SPY_CHAT_NOTIFICATION.send(session.getSessionHolder(),
                            "%player%", player.getName(), "%message%", message
                    );
                }

                event.setCancelled(true);
            }

            return;
        }


        // Ignoring if session is currently swapped.
        if (isSessionTarget && isControlling(session, player)) {
            return;
        }


        final boolean forceChat = ConfigData.Setting.ALWAYS_CHAT_AS_VICTIM.getValue(Boolean.class);
        if (isSessionTarget || !forceChat) {
            return;
        }

        if(message.toLowerCase().startsWith(bypassString)) {
            event.setMessage(message.substring(bypassString.length()));
            return;
        }

        queuedMessages.put(session.getSessionTarget().get().getUniqueId() + "==" + message, System.currentTimeMillis());
        event.setCancelled(true);

        schedulerProvider.createScheduler(task -> {
            session.getSessionTarget().get().chat(event.getMessage());
        });
    }
}
