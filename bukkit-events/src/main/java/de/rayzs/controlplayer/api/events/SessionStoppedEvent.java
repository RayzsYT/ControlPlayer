package de.rayzs.controlplayer.api.events;

import de.rayzs.controlplayer.api.session.Session;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SessionStoppedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Session session;

    public SessionStoppedEvent(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
