package de.rayzs.controlplayer.plugin.listener.listeners;

import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.utils.ExpireList;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.concurrent.TimeUnit;

public class EntityListener extends ControlPlayerListener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.isCancelled() || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }


        if (event.getEntity() instanceof Player player) {
            final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
            if (session == null || session.isStopped()) return;


            if (!isControlling(session, player)) {
                return;
            }

            final double healthAfterDamage = Math.max(0, player.getHealth() - event.getDamage());
            final Player controlled = session.getControlledPlayer().get();

            if (healthAfterDamage < 0.5) {
                event.setCancelled(true);
                controlled.damage(event.getDamage());

                return;
            }

            controlled.setHealth(healthAfterDamage);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityTargetLivingEntity(final EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {
            final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
            if (session == null || session.isStopped()) return;


            if (isControlling(session, player)) {
                final Player controller = session.getControllingPlayer().get();

                event.setTarget(controller);
                event.setCancelled(true);
            }
        }
    }


    private final ExpireList<Player> delayedPlayers = new ExpireList<>(10, TimeUnit.MILLISECONDS);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        if (event.getRightClicked() instanceof Sittable && event.getRightClicked() instanceof Tameable) {
            final Entity clickedEntity = event.getRightClicked();

            final Tameable tameable = (Tameable) clickedEntity;
            final Sittable sittable = (Sittable) clickedEntity;

            if (!tameable.isTamed() || tameable.getOwner() == null || tameable.getOwner().getName() == null) {
                return;
            }

            if (!tameable.getOwner().getName().equalsIgnoreCase(session.getControlledPlayer().get().getName())) {
                return;
            }

            if (delayedPlayers.contains(player)) {
                return;
            }

            delayedPlayers.addIgnoreIfContains(player);
            sittable.setSitting(!sittable.isSitting());

            session.getControllingPlayer().swingMainArm();
            session.getControlledPlayer().swingMainArm();
        }
    }
}
