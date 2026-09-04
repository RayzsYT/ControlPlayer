package de.rayzs.controlplayer.plugin.listener.listeners;

import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener extends ControlPlayerListener {

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
                final Player controlled = session.getControlledPlayer().get();

                event.setTarget(controlled);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (event.getAction().toString().contains("LEFT") && isSessionHolder(session, player) && session.isSemiSession()) {
            if (session.isControlSwapped() || !session.isControlSwapped() && player.isSneaking()) {
                session.countAndSwap();
            }
        }


        if (isControlling(session, player)) {
            if (event.getClickedBlock() == null) {
                return;
            }

            final String typeName = event.getClickedBlock().getType().name();
            if (typeName.contains("ENDER") && typeName.contains("CHEST") && event.getAction().name().contains("RIGHT")) {
                event.setCancelled(true);

                final Player controlled = session.getControlledPlayer().get();
                player.openInventory(controlled.getEnderChest());
            }

            return;
        }

        event.setCancelled(true);
    }
}
