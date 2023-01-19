package de.rayzs.controlplayer.api.listener;

import org.bukkit.entity.Player;

public interface ControlPlayerEvent {
    ControlPlayerEventType type();
    void execute(Player controller, Player victim);
}
