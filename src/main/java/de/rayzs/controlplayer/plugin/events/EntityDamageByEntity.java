package de.rayzs.controlplayer.plugin.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.util.Vector;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player;
        int instanceState;
        ControlInstance instance;

        if (event.getDamager() instanceof Player) {
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
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (event.getEntity() instanceof Player) {
                    player = (Player) event.getEntity();
                    if ((player.getHealth() - event.getDamage()) < 0.5) {
                        event.setCancelled(true);
                        player.damage(event.getDamage(), victim);
                    }
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            instanceState = ControlManager.getInstanceState(player);
            if (instanceState == 1) {
                ControlInstance controlInstance = ControlManager.getControlInstance(player);
                Player controller = controlInstance.controller();
                event.setCancelled(false);

                double healthAfterDamage = (player.getHealth() - event.getDamage());
                if (!event.isCancelled()) pushPlayer(controller, event.getDamager());

                if (healthAfterDamage < 0.5) return;
                controller.setHealth(healthAfterDamage);
            } else if (instanceState == 0) {
                double healthAfterDamage = (player.getHealth() - event.getDamage());
                if (healthAfterDamage < 0.5) {
                    ControlInstance controlInstance = ControlManager.getControlInstance(player);
                    Player victim = controlInstance.victim();
                    event.setCancelled(true);
                    victim.damage(event.getDamage(), event.getDamager());
                }
            }
        }
    }

    /*
      ORIGINAL CODE: https://gist.github.com/IllusionTheDev/b770d8e2ffd7a7d80351686c5ea46963
      I just changed some variables and add some checks
     */
    private void pushPlayer(Entity victim, Entity attacker) {
        Vector vector = new Vector(0.0, -0.078375, 0.0);
        if (attacker != null) {
            ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
            Location victimLocation = victim.getLocation(),
                    attackerLocation = attacker.getLocation();

            double deltaX = victimLocation.getX() - attackerLocation.getX(),
                    deltaZ = victimLocation.getZ() - attackerLocation.getZ(),
                    y = 0.36075;

            while (deltaX * deltaX + deltaZ * deltaZ < 1.0E-4) {
                deltaX = (threadLocalRandom.nextDouble()) - threadLocalRandom.nextDouble() * 0.01;
                deltaZ = (threadLocalRandom.nextDouble()) - threadLocalRandom.nextDouble() * 0.01;
            }

            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            deltaX = deltaX / distance * 0.40;
            deltaZ = deltaZ / distance * 0.40;

            if (attacker instanceof Player) {
                Player attackerAsPlayer = (Player) attacker;
                if (!attackerAsPlayer.isSneaking()) {
                    double yaw = attackerLocation.getYaw() * Math.PI / 180;
                    deltaX += -Math.sin(yaw) * 1.5;
                    deltaZ = Math.cos(yaw) * 1.5;
                    y += 0.1;
                    deltaX *= 0.6;
                    deltaZ *= 0.6;
                }
            }
            vector = new Vector(deltaX, y, deltaZ);
        }

        victim.setVelocity(vector);
    }
}