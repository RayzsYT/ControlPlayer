package de.rayzs.controlplayer.plugin.events;

import de.rayzs.controlplayer.api.control.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class EntityTargetLivingEntity implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player)) return;
        Player player = (Player) event.getTarget();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        ControlInstance instance = ControlManager.getControlInstance(player);
        event.setCancelled(true);

        Player newTarget = instance.controller();
        if(newTarget != null) event.setTarget(newTarget);
    }
}
