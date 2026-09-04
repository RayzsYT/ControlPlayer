package de.rayzs.controlplayer.api;

import de.rayzs.controlplayer.api.config.ConfigProvider;
import de.rayzs.controlplayer.api.player.PlayerConverter;
import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.session.SessionProvider;

public interface ControlPlayerAPI<P> {

    /**
     * Get session provider. To create, delete, and fetch controlling sessions.
     */
    SessionProvider<P> getSessionProvider();

    /**
     * Get scheduler provider.
     */
    SchedulerProvider getSchedulerProvider();

    /**
     * Convert a Player object to {@link de.rayzs.controlplayer.api.player.CPPlayer} object.
     */
    PlayerConverter<P> getPlayerConverter();

    /**
     * Get config provider.
     */
    ConfigProvider getConfigProvider();

    void reload();

    void info(final String message);
    void warning(final String message);
}
