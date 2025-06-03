 package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import java.util.Random;

 public class EntityDamage implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int instanceState = ControlManager.getInstanceState(player);
            if (instanceState != 0) return;

            if(event.isCancelled() || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

            double healthAfterDamage = (player.getHealth() - event.getDamage());
            ControlInstance controlInstance = ControlManager.getControlInstance(player);
            Player victim = controlInstance.victim();

            if(healthAfterDamage < 0.5) {
                event.setCancelled(true);
                victim.damage(event.getDamage());
            } else victim.setHealth(healthAfterDamage);
        }
    }
}