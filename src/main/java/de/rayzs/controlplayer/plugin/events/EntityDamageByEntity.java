package de.rayzs.controlplayer.plugin.events;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import java.lang.reflect.Field;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player;
        int instanceState;
        ControlInstance instance;

        if(event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            instanceState = ControlManager.getInstanceState(player);
            instance = ControlManager.getControlInstance(player);

            if (instanceState == 0) {
                if (instance.victim() == player) return;
                Player victim = instance.victim();
                try {
                    Class<?> clazz = event.getClass();
                    Field field = clazz.getDeclaredField("damager");
                    field.setAccessible(true);
                    field.set(event, victim);
                } catch (Exception exception) { exception.printStackTrace(); }
                if (event.getEntity() instanceof Player) {
                    player = (Player) event.getEntity();
                    if((player.getHealth() - event.getDamage()) < 0.5) {
                        event.setCancelled(true);
                        player.damage(event.getDamage(), victim);
                    }
                }
            }
        } else if(event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            instanceState = ControlManager.getInstanceState(player);
            if(instanceState == 1) event.setCancelled(true);
        }
    }
}
