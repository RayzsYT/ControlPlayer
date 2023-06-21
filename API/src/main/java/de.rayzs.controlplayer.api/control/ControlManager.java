package de.rayzs.controlplayer.api.control;

import de.rayzs.controlplayer.api.files.messages.*;
import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.api.listener.*;
import de.rayzs.controlplayer.api.packetbased.actionbar.Actionbar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControlManager {

    private static Plugin plugin = null;

    private static final HashMap<ControlInstance, ControlSwap> SWAPPED_INSTANCES = new HashMap<>();
    private static final HashMap<Player, Integer> LAST_FOODLEVEL = new HashMap<>(), LAST_LEVEL = new HashMap<>(), LAST_TOTALEXCPERIENCE = new HashMap<>();
    private static final HashMap<Player, Boolean> LAST_ALLOWED_FLIGHT = new HashMap<>(), LAST_FLYING = new HashMap<>();
    private static final HashMap<Player, ItemStack[]> LAST_INVENTORY = new HashMap<>(), LAST_ARMOR = new HashMap<>();
    private static final HashMap<Player, Double> LAST_HEALTH = new HashMap<>(), LAST_HEALTHSCALE = new HashMap<>();
    private static final HashMap<Player, Float> LAST_EXP = new HashMap<>(), LAST_EXHAUSTION = new HashMap<>();
    private static final HashMap<Player, Location> LAST_LOCATION = new HashMap<>();
    private static final HashMap<Player, GameMode> LAST_GAMEMODE = new HashMap<>();
    private static final HashMap<Player, List<Player>> PLAYER_WHO_CAN_SEE = new HashMap<>();

    private static final List<ControlInstance> INSTANCES = new ArrayList<>();
    private static boolean cancelCollision = true, apiMode, sendActionbar, returnInventory, returnLocation, returnHealth, returnFoodLevel, returnGamemode, returnFlight,returnLevel;

    private static Actionbar actionbar = new Actionbar();
    private static String controllingActionbarText = MessageManager.getMessage(MessageType.CONTROLLING_ACTIONBAR_TEXT),
            waitingActionbarText = MessageManager.getMessage(MessageType.WAITING_ACTIONBAR_TEXT);

    public static void load(Plugin bukkitPlugin) {
        plugin = bukkitPlugin;

        apiMode = (boolean) SettingsManager.getSetting(SettingType.APIMODE);
        sendActionbar = (boolean) SettingsManager.getSetting(SettingType.CONTROL_RUNNING_ACTIONBAR_ENABLED);
        returnInventory = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_INVENTORY);
        returnLocation = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_LOCATION);
        returnHealth = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_HEALTH);
        returnFoodLevel = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_FOODLEVEL);
        returnGamemode = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_GAMEMODE);
        returnFlight = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_FLIGHT);
        returnLevel = (boolean) SettingsManager.getSetting(SettingType.CONTROL_STOP_RETURN_LEVEL);

        int delay = (int) SettingsManager.getSetting(SettingType.CONTROL_RUNNING_SYNCDELAY);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(!INSTANCES.isEmpty()) INSTANCES.forEach(instance -> {
                ControlPlayerEventManager.call(ControlPlayerEventType.RUNNING, instance.controller(), instance.victim());
                if(!apiMode) instance.onTick();
            });
        }, 0, delay);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(!INSTANCES.isEmpty()) INSTANCES.forEach(instance -> {
                if(!apiMode) {
                    ControlSwap swap = getControlSwap(instance);
                    if(swap != null && swap.isEnabled()) {
                        boolean swapped = swap.isSwapped();
                        if(sendActionbar)
                            actionbar.execute(instance.controller(), (swapped ? waitingActionbarText : controllingActionbarText).replace("%player%", instance.victim().getName()));
                        (swapped ? instance.controller() : instance.victim()).teleport((swapped ? instance.victim() : instance.controller()));
                    } else {
                        actionbar.execute(instance.controller(), controllingActionbarText.replace("%player%", instance.victim().getName()));
                        instance.victim().teleport(instance.controller().getLocation());
                    }
                }
            });
        }, 0L, 0L);
    }

    public static ControlState createControlInstance(Player controller, Player victim, boolean useSwap) {
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
            @Override public void onTick() {
                ControlPlayerEventManager.call(ControlPlayerEventType.RUNNING, controller, victim);
                if(!apiMode) {
                    ControlSwap swap = getControlSwap(this);
                    if(swap != null && swap.isEnabled()) {
                        boolean swapped = swap.isSwapped();
                        syncPlayers(swapped ? victim : controller, swapped ? controller : victim, false);
                    } else syncPlayers(controller, victim, false);
                }
            }
        };

        SWAPPED_INSTANCES.put(controlInstance, new ControlSwap(useSwap));
        INSTANCES.add(controlInstance);

        ControlPlayerEventManager.call(ControlPlayerEventType.START, controller, victim);

        if(!apiMode) {
            List<Player> players_who_can_see = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(players -> {
                if(players.canSee(controller)) players_who_can_see.add(players);
            });
            PLAYER_WHO_CAN_SEE.put(controller, players_who_can_see);
            Bukkit.getOnlinePlayers().forEach(ControlManager::hideAllControllers);
            saveOrReturnController(controller, false);
            syncPlayers(controller, victim, true);

            if(cancelCollision) try {
                victim.spigot().setCollidesWithEntities(false);
            } catch (Throwable exception) {
                cancelCollision = false;
            }
        }

        return ControlState.SUCCESS;
    }

    public static void hideAllControllers(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(controllers -> getInstanceState(controllers) == 0).forEach(controllers -> {
            if(player.canSee(controllers)) {
                List<Player> player_who_can_see = PLAYER_WHO_CAN_SEE.get(controllers);
                player_who_can_see.add(player);
            }
            player.hidePlayer(controllers);
        });
    }

    public static boolean deleteControlInstance(Player player) {
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance
                -> instance.controller() == player || instance.victim() == player).findFirst();

        if(instanceOption.isPresent()) {
            ControlInstance controlInstance = instanceOption.get();
            Player victim = controlInstance.victim(), controller = controlInstance.controller();
            if(victim != null && controller != null) controlInstance.controller().showPlayer(controlInstance.victim());
            if(victim != null && cancelCollision) try {
                victim.spigot().setCollidesWithEntities(false);
            } catch (Throwable exception) {
                cancelCollision = false;
            }
            INSTANCES.remove(controlInstance);
            if(controller != null) {
                ControlPlayerEventManager.call(ControlPlayerEventType.STOP, controller, victim);
                if(!apiMode) saveOrReturnController(controller, true);
            }
            return true;
        }
        return false;
    }

    public static void deleteSafeControlInstance(Player player) {
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance
                -> instance.controller() == player || instance.victim() == player).findFirst();

        if(instanceOption.isPresent()) {
            ControlInstance controlInstance = instanceOption.get();
            Player victim = controlInstance.victim(), controller = controlInstance.controller();
            if(victim != null && controller != null) controlInstance.controller().showPlayer(controlInstance.victim());
            if(victim != null && cancelCollision) try {
                victim.spigot().setCollidesWithEntities(false);
            } catch (Throwable exception) {
                cancelCollision = false;
            }
            INSTANCES.remove(controlInstance);
            if(controller == null) return;
            ControlPlayerEventManager.call(ControlPlayerEventType.STOP, controller, victim);
            if(!apiMode) {
                controller.setHealth(20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> saveOrReturnController(controller, true), 1);
            }
        }
    }

    public static ControlInstance getControlInstance(Player player) {
        Optional<ControlInstance> instanceOption = INSTANCES.stream().filter(instance
                -> instance.controller() == player || instance.victim() == player).findFirst();
        return instanceOption.orElse(null);
    }

    public static ControlSwap getControlSwap(ControlInstance instance) {
        return SWAPPED_INSTANCES.get(instance);
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
            controller.hidePlayer(victim);
            controller.teleport(victim.getLocation());
            syncPlayerSources(controller, victim);
            return;
        }
        syncPlayerSources(victim, controller);
    }

    protected static void syncPlayerSources(Player player, Player sourcePlayer) {
        player.getInventory().setContents(sourcePlayer.getInventory().getContents());
        player.getInventory().setArmorContents(sourcePlayer.getInventory().getArmorContents());
        if(!(sourcePlayer.getHealth() < 0.5 || sourcePlayer.getHealth() > 20)) player.setHealth(sourcePlayer.getHealth());
        player.setFoodLevel(sourcePlayer.getFoodLevel());
        player.setLevel(sourcePlayer.getLevel());
        player.setExp(sourcePlayer.getExp());
        player.setFireTicks(sourcePlayer.getFireTicks());
        player.setExhaustion(sourcePlayer.getExhaustion());
        player.setAllowFlight(sourcePlayer.getAllowFlight());
        player.setGameMode(sourcePlayer.getGameMode());
        player.getInventory().setHeldItemSlot(sourcePlayer.getInventory().getHeldItemSlot());
        if(sourcePlayer.getActivePotionEffects().size() > 0) sourcePlayer.getActivePotionEffects().forEach(player::addPotionEffect);
        else player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    protected static void saveOrReturnController(Player player, boolean load) {
        if(load) {
            if (returnInventory) {
                player.getInventory().setContents(LAST_INVENTORY.get(player));
                player.getInventory().setArmorContents(LAST_ARMOR.get(player));
            }
            if (returnFlight) {
                player.setAllowFlight(LAST_ALLOWED_FLIGHT.get(player));
                player.setFlying(LAST_FLYING.get(player));
            }
            if (returnLevel) {
                player.setLevel(LAST_LEVEL.get(player));
                player.setExhaustion(LAST_EXHAUSTION.get(player));
                player.setExp(LAST_EXP.get(player));
                player.setTotalExperience(LAST_TOTALEXCPERIENCE.get(player));
            }
            if (returnHealth) {
                player.setHealth(LAST_HEALTH.get(player));
                player.setHealthScale(LAST_HEALTHSCALE.get(player));
            }
            if (returnLocation) player.teleport(LAST_LOCATION.get(player));
            if (returnFoodLevel) player.setFoodLevel(LAST_FOODLEVEL.get(player));
            if (returnGamemode) player.setGameMode(LAST_GAMEMODE.get(player));
            PLAYER_WHO_CAN_SEE.get(player).stream().filter(OfflinePlayer::isOnline).forEach(players -> players.showPlayer(player));
            PLAYER_WHO_CAN_SEE.remove(player);
        } else {
            if (returnInventory) {
                LAST_INVENTORY.put(player, player.getInventory().getContents());
                LAST_ARMOR.put(player, player.getInventory().getArmorContents());
            }
            if (returnFlight) {
                LAST_ALLOWED_FLIGHT.put(player, player.getAllowFlight());
                LAST_FLYING.put(player, player.isFlying());
            }
            if (returnLevel) {
                LAST_LEVEL.put(player, player.getLevel());
                LAST_EXHAUSTION.put(player, player.getExhaustion());
                LAST_EXP.put(player, player.getExp());
                LAST_TOTALEXCPERIENCE.put(player, player.getTotalExperience());
            }
            if (returnHealth) {
                LAST_HEALTH.put(player, player.getHealth());
                LAST_HEALTHSCALE.put(player, player.getHealthScale());
            }
            if (returnLocation) LAST_LOCATION.put(player, player.getLocation());
            if (returnFoodLevel) LAST_FOODLEVEL.put(player, player.getFoodLevel());
            if (returnGamemode) LAST_GAMEMODE.put(player, player.getGameMode());
        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}