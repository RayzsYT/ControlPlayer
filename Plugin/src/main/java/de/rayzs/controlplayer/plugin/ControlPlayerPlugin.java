package de.rayzs.controlplayer.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.rayzs.controlplayer.api.adapter.LuckPermsAdapter;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import de.rayzs.controlplayer.api.hierarchy.HierarchyManager;
import de.rayzs.controlplayer.api.specific.SpecificControlManager;
import de.rayzs.controlplayer.api.version.ServerVersion;
import de.rayzs.controlplayer.api.web.WebConnection;
import de.rayzs.controlplayer.plugin.bstats.Metrics;
import de.rayzs.controlplayer.plugin.commands.ControlPlayerCommand;
import de.rayzs.controlplayer.plugin.commands.ControlPlayerFixCommand;
import de.rayzs.controlplayer.plugin.commands.ControlPlayerOtherCommand;
import de.rayzs.controlplayer.plugin.commands.ControlPlayerReloadCommand;
import de.rayzs.controlplayer.plugin.commands.ControlPlayerStopCommand;
import de.rayzs.controlplayer.plugin.commands.SilentControlPlayerCommand;
import de.rayzs.controlplayer.plugin.events.BlockBreak;
import de.rayzs.controlplayer.plugin.events.BlockPlace;
import de.rayzs.controlplayer.plugin.events.EntityDamage;
import de.rayzs.controlplayer.plugin.events.EntityDamageByEntity;
import de.rayzs.controlplayer.plugin.events.EntityTargetLivingEntity;
import de.rayzs.controlplayer.plugin.events.InventoryInteraction;
import de.rayzs.controlplayer.plugin.events.PlayerAchievement;
import de.rayzs.controlplayer.plugin.events.PlayerAnimation;
import de.rayzs.controlplayer.plugin.events.PlayerChangeWorld;
import de.rayzs.controlplayer.plugin.events.PlayerChat;
import de.rayzs.controlplayer.plugin.events.PlayerCommandPreProcess;
import de.rayzs.controlplayer.plugin.events.PlayerDeath;
import de.rayzs.controlplayer.plugin.events.PlayerDropItem;
import de.rayzs.controlplayer.plugin.events.PlayerInteract;
import de.rayzs.controlplayer.plugin.events.PlayerInteractAtEntity;
import de.rayzs.controlplayer.plugin.events.PlayerJoin;
import de.rayzs.controlplayer.plugin.events.PlayerPickupItem;
import de.rayzs.controlplayer.plugin.events.PlayerQuit;
import de.rayzs.controlplayer.plugin.events.PlayerTeleport;
import de.rayzs.controlplayer.plugin.events.PlayerToggleFlight;
import de.rayzs.controlplayer.plugin.events.PlayerToggleSneak;
import de.rayzs.controlplayer.plugin.events.PlayerToggleSprint;

public class ControlPlayerPlugin extends JavaPlugin {

    private static ControlPlayerPlugin instance;

    private boolean latestVersion = true;
    private int updaterTaskId;
    private final WebConnection web = new WebConnection();

    private static final List<Command> registeredCommands = new ArrayList<>();
    private static CommandMap commandMap = null;

    private final Class<?>[] listenerClasses = {
            PlayerChangeWorld.class, PlayerDeath.class, PlayerInteract.class, PlayerInteractAtEntity.class, PlayerAnimation.class,
            EntityDamage.class, EntityDamageByEntity.class, EntityTargetLivingEntity.class,
            PlayerToggleFlight.class, PlayerToggleSneak.class, PlayerToggleSprint.class,
            PlayerPickupItem.class, PlayerDropItem.class, InventoryInteraction.class,
            PlayerJoin.class, PlayerQuit.class,
            BlockBreak.class, BlockPlace.class,
            PlayerChat.class, PlayerCommandPreProcess.class, PlayerTeleport.class/*, PlayerMove.class*/
    };


    @Override
    public void onEnable() {
        instance = this;
        ControlManager.load(this);

        accessSimpleCommandMap();

        startUpdaterTask();
        registerCommands();
        registerEvents();
        new Metrics(this, 15651);

        if(Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPermsAdapter.initialize();
            HierarchyManager.initialize();
            SpecificControlManager.initialize();
        }
    }

    @Override
    public void onDisable() {
        unregisterCommands();
    }

    public static ControlPlayerPlugin getInstance() {
        return instance;
    }

    public boolean isLatestVersion() {
        return latestVersion;
    }

    public static void registerCommands() {
        registerCommand(new ControlPlayerCommand("controlplayer", "Take full control over a player", "/controlplayer <player>", (ArrayList<String>) SettingsManager.getSetting(SettingType.COMMANDALIASES_CONTROL)));
        registerCommand(new ControlPlayerStopCommand("controlplayerstop", "Force someone to stop controlling", "/controlplayerstop <controller>", Arrays.asList("cps", "cpstop")));
        registerCommand(new ControlPlayerOtherCommand("controlplayerother", "Force someone to control another player", "/controlplayerother <controller> <victim>", Arrays.asList("cpo", "cpother")));
        registerCommand(new SilentControlPlayerCommand("silentcontrolplayer", "Activate the toggle-mode to control a player", "/silentcontrolmode <player>", (ArrayList<String>) SettingsManager.getSetting(SettingType.COMMANDALIASES_SILENTCONTROL)));
        registerCommand(new ControlPlayerReloadCommand("controlplayerreload", "Reload all files", "/controlplayerreload", (ArrayList<String>) SettingsManager.getSetting(SettingType.COMMANDALIASES_RELOAD)));
        registerCommand(new ControlPlayerFixCommand("controlplayerfix", "Fix players or yourself", "/controlplayerfix <optional: player>", (ArrayList<String>) SettingsManager.getSetting(SettingType.COMMANDALIASES_FIX)));
    }

    public static void unregisterCommands() {
        if(commandMap == null) return;
        for (Command command : registeredCommands) command.unregister(commandMap);
    }

    protected void registerEvents() {
        for (Class<?> clazz : listenerClasses) {
            try {
                Listener listener = (Listener) clazz.newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            }catch (InstantiationException | IllegalAccessException exception) {
                getLogger().warning("Could not register the listener-class [" + clazz.getSimpleName() + "]!");
                getLogger().warning("Error message:");
                exception.printStackTrace();
            }
        }

        if (ServerVersion.getInstance().getMinor() >= 18) {
            getServer().getPluginManager().registerEvents(new PlayerAchievement(), this);
        }
    }

    protected static void registerCommand(Command command) {
        registeredCommands.add(command);
        commandMap.register(command.getName(), command);
    }

    protected void accessSimpleCommandMap() {
        try {
            final Class<? extends Server> clazz = Bukkit.getServer().getClass();
            final Field field = clazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (Throwable throwable) {
            getLogger().warning("Could not access CommandMap!");
        }
    }

    protected void startUpdaterTask() {
        Object updateObject = SettingsManager.getSetting(SettingType.UPDATER_ENABLED);
        boolean update = (updateObject == null || (boolean) updateObject);
        if (!update)
            return;
        Object delayObject = SettingsManager.getSetting(SettingType.UPDATER_DELAY);
        int delay = (delayObject == null) ? 18000 : (int) delayObject;
        updaterTaskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            String result = web.connect("https://www.rayzs.de/products/controlplayer/version/version.txt").getResult();
            if (!result.equals(getDescription().getVersion())) {
                Bukkit.getScheduler().cancelTask(this.updaterTaskId);
                if (result.equals("unknown")) {
                    getLogger().warning("Failed reaching web host! (firewall enabled? website down?)");
                } else if (result.equals("exception")) {
                    getLogger().warning("Failed creating web instance! (outdated java version?)");
                } else {
                    getLogger().warning("You're using an outdated version of this plugin!");
                    getLogger().warning("Please update it on: https://www.rayzs.de/products/controlplayer/page");
                }
                this.latestVersion = false;
            }
        }, 20L, delay);
    }
}