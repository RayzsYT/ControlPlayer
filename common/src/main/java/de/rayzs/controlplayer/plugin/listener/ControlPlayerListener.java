package de.rayzs.controlplayer.plugin.listener;

import de.rayzs.controlplayer.api.ControlPlayer;
import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.session.SessionProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ControlPlayerListener implements Listener {

    protected final ControlPlayerAPI<Player> api = ControlPlayer.get();
    protected final SessionProvider<Player> sessionProvider = api.getSessionProvider();
    protected final SchedulerProvider schedulerProvider = api.getSchedulerProvider();


    protected boolean isControlling(final Session<Player> session, final Player player) {
        if (session == null || session.isStopped()) return false;

        return session.getControllingPlayer().get().getUniqueId().equals(player.getUniqueId());
    }

    protected boolean isBeingControlled(final Session<Player> session, final Player player) {
        if (session == null || session.isStopped()) return false;

        return session.getControlledPlayer().get().getUniqueId().equals(player.getUniqueId());
    }

    protected boolean isSessionHolder(final Session<Player> session, final Player player) {
        if (session == null || session.isStopped()) return false;

        return session.getSessionHolder().get().getUniqueId().equals(player.getUniqueId());
    }

    protected boolean isSessionTarget(final Session<Player> session, final Player player) {
        if (session == null || session.isStopped()) return false;

        return session.getSessionTarget().get().getUniqueId().equals(player.getUniqueId());
    }

    protected void hideAllControllers(final Player player) {
        sessionProvider.getAllRunningSessions().forEach(session -> {
            if (session.getSessionHolder().get() instanceof Player controller) {
                player.hidePlayer(controller);
            }
        });
    }
}
