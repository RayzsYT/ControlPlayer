package de.rayzs.controlplayer.api.session;

import de.rayzs.controlplayer.api.player.CPPlayer;

import java.util.List;
import java.util.UUID;

public interface SessionProvider<P> {

    /**
     * Create a session. A session is an instance of the controller and controlled victim.
     *
     * @param holder Player who holds the session.
     * @param target Target player.
     *
     * @return Returns the created session if successful. Returns {@code null} if it's not possible.
     */
    Session createAndReturnSession(
            final CPPlayer holder,
            final CPPlayer target
    );

    /**
     * Create a semi-control session. A session is an instance of the controller and controlled victim.
     *
     * @param holder Player who holds the session.
     * @param target Target player.
     *
     * @return Returns the created session if successful. Returns {@code null} if it's not possible.
     */
    Session createAndReturnSemiSession(
            final CPPlayer holder,
            final CPPlayer target
    );

    /**
     * Destroys a session and stops all processes related to it.
     *
     * @param session The session to destroy.
     * @return Returns {@link Boolean#TRUE} if successful. Returns {@link Boolean#FALSE} otherwise.
     */
    boolean destroySession(
            final Session session
    );

    /**
     * Check whether a player can control another player.
     *
     * @param controllerUUID Controlling player uuid.
     * @param victimUUID Controlled player uuid.
     * @return Whether the victim can be controlled by the player or not.
     */
    boolean canControl(final UUID controllerUUID, final UUID victimUUID);

    /**
     * Get the currently running session related to that player.
     *
     * @param player Player.
     * @return Returns the to the player related {@link Session <P>} instance. Returns {@code null} if there's none.
     */
    Session getSession(final CPPlayer<P> player);

    /**
     * Get the currently running session related to that player.
     *
     * @param uuid UUID of the player.
     * @return Returns the to the player related {@link Session <P>} instance. Returns {@code null} if there's none.
     */
    Session getSession(final UUID uuid);

    /**
     * A list of all currently running sessions.
     */
    List<Session> getAllRunningSessions();

}
