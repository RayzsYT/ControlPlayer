package de.rayzs.controlplayer.api.control;

import org.bukkit.entity.Player;

public interface ControlInstance {
    Player victim();
    Player controller();
    void onTick();
}