package de.rayzs.controlplayer.api.listener;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ControlPlayerEventManager {
    public static List<ControlPlayerEvent> events = new ArrayList<>();

    public static void register(ControlPlayerEvent event) {
        if (events.contains(event))
            return;
        events.add(event);
    }

    public static void unregister(ControlPlayerEvent event) {
        if (!events.contains(event))
            return;
        events.remove(event);
    }

    public static void call(ControlPlayerEventType type, Player controller, Player victim) {
        events.stream().filter(event -> (event.type() == type)).forEach(event -> event.execute(controller, victim));
    }
}
