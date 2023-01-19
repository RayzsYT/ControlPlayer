package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.packetbased.animation.ArmSwingAnimation;
import org.bukkit.event.player.PlayerAnimationEvent;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class PlayerAnimation extends ArmSwingAnimation implements Listener {

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        String animationTypeAsString = event.getAnimationType().toString().toLowerCase();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(animationTypeAsString.contains("arm") && animationTypeAsString.contains("swing"))
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;
                Player victim = instance.victim();
                execute(victim);
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }
}
