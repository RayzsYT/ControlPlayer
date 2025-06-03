package de.rayzs.controlplayer.api.control;

import de.rayzs.controlplayer.api.counter.Counter;
import org.bukkit.entity.Player;

public class ControlSwap {

    private final Counter moveCounter;
    private final boolean enabled;
    private boolean swapped;

    public ControlSwap(boolean enabled) {
        this.enabled = enabled;
        this.swapped = true;
        this.moveCounter = new Counter(500);
    }

    public void checkAndSwap(Player player) {
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        if(instance.controller() != player || !player.isSneaking() && !swapped) return;
        moveCounter.add();
        if(moveCounter.getCount() >= 2) {
            moveCounter.set(0);
            swapped = !swapped;
            player.setSneaking(false);
        }
    }

    public boolean isSwapped() { return swapped; }
    public boolean isEnabled() { return enabled; }
}
