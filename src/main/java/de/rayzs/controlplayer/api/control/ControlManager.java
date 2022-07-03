package de.rayzs.controlplayer.api.control;

import java.util.*;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControlManager {

    private static final List<ControlInstance> INSTANCES = new ArrayList<>();
    private static final HashMap<Player, Location> LAST_LOCATIONS = new HashMap<>();

    static {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ControlPlayerPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(!INSTANCES.isEmpty()) INSTANCES.forEach(ControlInstance::onTick);
            }
        }, 0, 0);
    }

    public static ControlState createControlInstance(Player controller, Player victim) {
        if(controller == null
                || victim == null
                || !controller.isOnline()
                || !victim.isOnline())
            return ControlState.OFFLINE;

        int controllerState = getInstanceState(controller), victimState = getInstanceState(victim);
        if(controllerState != -1) return ControlState.ALREADY_CONTROLLING;
        else if(victimState != -1) return ControlState.ALREADY_CONTROLLED;

        ControlInstance controlInstance = new ControlInstance() {
            @Override public Player victim() { return victim; }
            @Override public Player controller() { return controller; }
            @Override public void onTick() { syncPlayers(controller, victim, false); }
        };

        syncPlayers(controller, victim, true);

        INSTANCES.add(controlInstance);
        victim.spigot().setCollidesWithEntities(false);
        Bukkit.getOnlinePlayers().forEach(ControlManager::hideAllControllers);
        return ControlState.SUCCESS;
    }

    public static void hideAllControllers(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(players -> getInstanceState(players) == 0).forEach(player::hidePlayer);
    }

    public static boolean deleteControlInstance(Player player) {
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance
                -> instance.controller() == player || instance.victim() == player).findFirst();

        if(instanceOption.isPresent()) {
            ControlInstance controlInstance = instanceOption.get();
            Player victim = controlInstance.victim(), controller = controlInstance.controller();
            if(victim != null && controller != null) controlInstance.controller().showPlayer(controlInstance.victim());
            if(victim != null) victim.spigot().setCollidesWithEntities(true);
            INSTANCES.remove(controlInstance);
            if(controller != null) {
                Location lastLocation = LAST_LOCATIONS.get(controller);
                player.teleport(lastLocation);
                Bukkit.getOnlinePlayers().stream().filter(players -> !players.canSee(player)).forEach(players -> players.showPlayer(player));
            }
            return true;
        }
        return false;
    }

    public static ControlInstance getControlInstance(Player player) {
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance
                -> instance.controller() == player || instance.victim() == player).findFirst();
        return instanceOption.orElse(null);
    }

    public static int getInstanceState(Player player) {
        AtomicBoolean isOwner = new AtomicBoolean(false);
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance -> {
            if(instance.controller() == player) {
                isOwner.set(true);
                return true;
            } else return false;
        }).findFirst();
        return (instanceOption.isPresent() && isOwner.get()) ? 0 : instanceOption.isPresent() && !isOwner.get() ? 1 : getControlInstance(player) != null && !isOwner.get() ? 1 : -1;
    }

    protected static void syncPlayers(Player controller, Player victim, boolean start) {
        if(start) {
            LAST_LOCATIONS.put(controller, controller.getLocation());
            controller.hidePlayer(victim);
            syncPlayerSources(controller, victim);
            return;
        }
        syncPlayerSources(victim, controller);
    }

    protected static void syncPlayerSources(Player player, Player sourcePlayer) {
        player.teleport(sourcePlayer);
        player.getInventory().setContents(sourcePlayer.getInventory().getContents());
        player.getInventory().setArmorContents(sourcePlayer.getInventory().getArmorContents());
        player.setHealth(sourcePlayer.getHealth());
        player.setLevel(sourcePlayer.getLevel());
        player.setExp(sourcePlayer.getExp());
        player.setExhaustion(sourcePlayer.getExhaustion());
        player.setAllowFlight(sourcePlayer.getAllowFlight());
        player.setGameMode(sourcePlayer.getGameMode());
        player.getInventory().setHeldItemSlot(sourcePlayer.getInventory().getHeldItemSlot());
        if(sourcePlayer.getActivePotionEffects().size() > 0) sourcePlayer.getActivePotionEffects().forEach(player::addPotionEffect);
        else player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }
}