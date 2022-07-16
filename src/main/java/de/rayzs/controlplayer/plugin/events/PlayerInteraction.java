package de.rayzs.controlplayer.plugin.events;

import org.bukkit.Bukkit;
import de.rayzs.controlplayer.api.packetbased.animation.ArmSwingAnimation;
import de.rayzs.controlplayer.api.control.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.lang.reflect.Field;

public class PlayerInteraction implements Listener {

    private final ArmSwingAnimation armSwingAnimation;

    public PlayerInteraction() {
        this.armSwingAnimation = new ArmSwingAnimation(Bukkit.getServer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 0) return;

        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance.victim() == player) return;

        Player victim = instance.victim();

        String blockType = event.getClickedBlock().getType().toString();
        String[] triggerTypes = {"CHEST", "SHULKER", "DISPENSER", "DROPPER", "FURNACE", "CRAFTING", "HOPPER", "LEVER", "BUTTON", "REPEATER", "COMPARATOR"};
        for (String triggerType : triggerTypes) {
            if(!blockType.contains(triggerType)) continue;
            armSwingAnimation.execute(victim);
            break;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                armSwingAnimation.execute(victim);
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                armSwingAnimation.execute(victim);
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player || event.getRightClicked() == null) return;

                String[] triggerTypes = {"HORSE", "PIG", "SHEEP", "MINECART"};
                for (String triggerType : triggerTypes) {
                    if(!event.getRightClicked().getType().toString().contains(triggerType)) continue;
                    Player victim = instance.victim();
                    armSwingAnimation.execute(victim);
                    break;
                }
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        if(instanceState != 1) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player;
        int instanceState;
        ControlInstance instance;

        if(event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
            instanceState = ControlManager.getInstanceState(player);
            instance = ControlManager.getControlInstance(player);

            if (instanceState == 0) {
                if (instance.victim() == player) return;
                Player victim = instance.victim();
                armSwingAnimation.execute(victim);
                try {
                    Class<?> clazz = event.getClass();
                    Field field = clazz.getDeclaredField("damager");
                    field.setAccessible(true);
                    field.set(event, victim);
                } catch (Exception exception) { exception.printStackTrace(); }
                if (event.getEntity() instanceof Player) {
                    player = (Player) event.getEntity();
                    if((player.getHealth() - event.getDamage()) < 0.5) {
                        event.setCancelled(true);
                        player.damage(20.0D, victim);
                    }
                }
            }
        } else if(event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            instanceState = ControlManager.getInstanceState(player);
            if(instanceState == 1) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int instanceState = ControlManager.getInstanceState(player);
            if(instanceState == 1) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                victim.setSneaking(event.isSneaking());
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                victim.setSprinting(event.isSprinting());
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        int instanceState = ControlManager.getInstanceState(player);
        ControlInstance instance = ControlManager.getControlInstance(player);
        switch (instanceState) {
            case 0:
                if(instance.victim() == player) return;

                Player victim = instance.victim();
                victim.setFlying(event.isFlying());
                break;
            case 1:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
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
