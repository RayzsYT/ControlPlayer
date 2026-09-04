package de.rayzs.controlplayer.plugin.listener.listeners;

import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.utils.VersionComparer;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import de.rayzs.controlplayer.plugin.listener.ControlPlayerListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Arrays;

public class PlayerListener extends ControlPlayerListener {


    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player bukkitPlayer = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(bukkitPlayer.getUniqueId());
        if (session == null || session.isStopped()) return;


        hideAllControllers(bukkitPlayer);


        if (!(bukkitPlayer.isOp() || bukkitPlayer.hasPermission("controlplayer.notify")) || VersionComparer.get().isOutdated()) {
            return;
        }

        schedulerProvider.createScheduler(task -> {
            if (!bukkitPlayer.isOnline()) return;


            final CPPlayer<Player> player = api.getPlayerConverter().convertPlayer(bukkitPlayer);
            ConfigData.Message.UPDATE_OUTDATED_MESSAGE.send(player);
        }, 30);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        sessionProvider.destroySession(session);

        if (session.getSessionTarget().isSame(player.getUniqueId())) {
            ConfigData.Message.PLAYER_LEFT.send(session.getSessionHolder(), "%player%", player.getName());
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (isBeingControlled(session, player)) {
            if (event.getReason().equals("Flying is not enabled on this server")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAnimation(final PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        final PlayerAnimationType animationType = event.getAnimationType();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (animationType == PlayerAnimationType.ARM_SWING && isSessionHolder(session, player) && session.isSemiSession()) {
            if (session.isControlSwapped() || !session.isControlSwapped() && player.isSneaking()) {
                session.countAndSwap();
            }
        }

        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        if (!isSessionHolder(session, player)) return;

        if (animationType == PlayerAnimationType.OFF_ARM_SWING) {
            session.getControlledPlayer().swingOffArm();
        } else {
            session.getControlledPlayer().swingMainArm();
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerChangeWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        hideAllControllers(player);
    }


    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (!ConfigData.Setting.SYNC_TELEPORTATION.getValue(Boolean.class)) {
            return;
        }

        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped() || event.getTo() == null) return;

        final Location toLoc = event.getTo(), fromLoc = event.getFrom();
        if (!isBeingControlled(session, player)) return;

        if (!Arrays.asList(PlayerTeleportEvent.TeleportCause.COMMAND, PlayerTeleportEvent.TeleportCause.PLUGIN).contains(event.getCause())) {
            return;
        }

        final Player controller = session.getControllingPlayer().get();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN
                && (fromLoc.getWorld().equals(toLoc.getWorld()) && fromLoc.distance(toLoc) < 5)
        ) {
            return;
        }

        controller.teleport(player.getLocation());
        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if (session == null || session.isStopped()) return;


        if (session.getSessionHolder().isSame(player.getUniqueId())) {
            event.setDeathMessage(null);
            return;
        }


        sessionProvider.destroySession(session);
        ConfigData.Message.PLAYER_DIED.send(session.getSessionHolder(), "%player%", player.getName());
    }


    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player bukkitPlayer = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(bukkitPlayer.getUniqueId());
        if (session == null || session.isStopped()) return;

        final CPPlayer<Player> controller = session.getControllingPlayer();
        final CPPlayer<Player> victim = session.getControlledPlayer();

        victim.teleport(controller);
    }


    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;


        if (isBeingControlled(session, player) && !ConfigData.Setting.VICTIM_CAN_DROP_ITEMS.getValue(Boolean.class)) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;

        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        final Player controlled = session.getControlledPlayer().get();
        controlled.setFlying(event.isFlying());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;

        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        final Player controlled = session.getControlledPlayer().get();
        controlled.getInventory().setHeldItemSlot(event.getNewSlot());
    }


    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;


        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        final Player controlled = session.getControlledPlayer().get();
        controlled.setSneaking(event.isSneaking());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        final Player player = event.getPlayer();
        final Session<Player> session = sessionProvider.getSession(player.getUniqueId());
        if(session == null || session.isStopped() || event.isCancelled()) return;

        if (isBeingControlled(session, player)) {
            event.setCancelled(true);
            return;
        }

        final Player controlled = session.getControlledPlayer().get();
        controlled.setSprinting(event.isSprinting());
    }
}
