package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.counter.Counter;
import de.rayzs.controlplayer.api.packetbased.animation.ArmSwingAnimation;
import org.bukkit.event.player.PlayerAnimationEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import java.util.HashMap;

public class PlayerAnimation extends ArmSwingAnimation implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        String animationTypeAsString = event.getAnimationType().toString().toLowerCase();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null || !(animationTypeAsString.contains("arm") && animationTypeAsString.contains("swing"))) return;
        if(!animationTypeAsString.contains("arm") && !animationTypeAsString.contains("swing")) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();

        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) event.setCancelled(true);
        else if(!useSwap) execute(instance.victim());
    }
}
