package de.rayzs.controlplayer.plugin.listener.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AdvancementListener extends ControlPlayerListener {

    @EventHandler
    public void onSaveAchievement(final PlayerAdvancementCriterionGrantEvent event) {
        final Player player = event.getPlayer();
        final Session session = sessionProvider.getSession(player.getUniqueId());

        if (session == null || session.isStopped() || isControlling(session, player)) {
            return;
        }

        event.setCancelled(true);
    }
}
