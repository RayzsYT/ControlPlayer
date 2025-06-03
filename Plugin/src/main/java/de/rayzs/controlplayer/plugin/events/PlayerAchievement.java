package de.rayzs.controlplayer.plugin.events;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerAchievement implements Listener {

    @EventHandler
    public void onSaveAchievement(PlayerAdvancementCriterionGrantEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);

        if(instance == null) return;

        if (instance.controller() != player)
            return;

        event.setCancelled(true);
    }
}
