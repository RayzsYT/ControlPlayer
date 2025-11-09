package de.rayzs.controlplayer.plugin.events;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import de.rayzs.controlplayer.api.packetbased.animation.ArmSwingAnimation;
import de.rayzs.controlplayer.api.utils.ExpireList;

public class PlayerInteractAtEntity extends ArmSwingAnimation implements Listener {

    private final ExpireList<Player> delayedPlayers = new ExpireList<>(10, TimeUnit.MILLISECONDS);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null)
            return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1)
            event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null)
            return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        int instanceState = ControlManager.getInstanceState(player);
        boolean useSwap = swap.isEnabled() && swap.isSwapped();
        if (useSwap && instanceState == 0 || !useSwap && instanceState == 1) {
            event.setCancelled(true);
            return;
        }

        if (useSwap && instanceState == 1 || !useSwap && instanceState == 0) {

            if (event.getRightClicked() instanceof Sittable && event.getRightClicked() instanceof Tameable) {
                Entity clickedEntity = event.getRightClicked();

                Tameable tameable = (Tameable) clickedEntity;
                Sittable sittable = (Sittable) clickedEntity;

                if (!tameable.isTamed() || tameable.getOwner() == null || tameable.getOwner().getName() == null) {
                    return;
                }

                ControlInstance controlInstance = ControlManager.getControlInstance(player);
                if (controlInstance == null) {
                    return;
                }

                if (!tameable.getOwner().getName().equalsIgnoreCase(controlInstance.victim().getName())) {
                    return;
                }

                if (delayedPlayers.contains(player)) {
                    return;
                }

                delayedPlayers.addIgnoreIfContains(player);

                sittable.setSitting(!sittable.isSitting());

                execute(instance.victim(), "");
                execute(instance.controller(), "");

            }

        }
    }
}
