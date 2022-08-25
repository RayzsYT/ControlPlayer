package de.rayzs.controlplayer.plugin;

import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.plugin.commands.*;
import de.rayzs.controlplayer.api.web.WebConnection;
import de.rayzs.controlplayer.plugin.bstats.Metrics;
import de.rayzs.controlplayer.plugin.events.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

public class ControlPlayerPlugin extends JavaPlugin {

    private static ControlPlayerPlugin instance;

    private boolean latestVersion = true;
    private int updaterTaskId;
    private final WebConnection web = new WebConnection();
    private final String[] mainCommandNames = {"cp", "controlplayer", "cplayer", "controlp"},
            reloadCommandNames = {"controlplayerreload", "controlplayerr", "cpr", "cpreload"};

    private final AtomicInteger urlId = new AtomicInteger();
    private final String path = "https://www.rayzs.de/products/controlplayer/version/";
    private final String[] urls = {
            "index.php", // I can't completely switch to the new url bcs many people are using my old system and need the old system.
            "raw/version.txt" // A backup page to get the current version.
    };

    private final Class<?>[] listenerClasses = {
            PlayerChangeWorld.class, PlayerDeath.class, PlayerInteract.class, PlayerInteractAtEntity.class, PlayerAnimation.class,
            EntityDamage.class, EntityDamageByEntity.class, EntityTargetLivingEntity.class,
            PlayerToggleFlight.class, PlayerToggleSneak.class, PlayerToggleSprint.class,
            PlayerPickupItem.class, PlayerDropItem.class,
            PlayerJoin.class, PlayerQuit.class,
            BlockBreak.class, BlockPlace.class,
            AsyncPlayerChat.class,
    };


    @Override
    public void onEnable() {
        instance = this;
        startUpdaterTask();
        registerCommands();
        registerEvents();
        new Metrics(this, 15651);
    }

    @Override
    public void onDisable() { }

    public static ControlPlayerPlugin getInstance() {
        return instance;
    }

    public boolean isLatestVersion() {
        return latestVersion;
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
    }

    protected void registerCommands() {
        ControlPlayerCommand mainCommandClass = new ControlPlayerCommand();
        ControlPlayerReloadCommand reloadCommandClass = new ControlPlayerReloadCommand();
        ControlPlayerTabCompleter tabCompleterClass = new ControlPlayerTabCompleter();

        for (String commandName : mainCommandNames) {
            PluginCommand command = getCommand(commandName);
            command.setExecutor(mainCommandClass);
            command.setTabCompleter(tabCompleterClass);
        }

        for (String commandName : reloadCommandNames) {
            PluginCommand command = getCommand(commandName);
            command.setExecutor(reloadCommandClass);
        }
    }

    protected void startUpdaterTask() {
        Object updateObject = SettingsManager.getSetting(SettingType.UPDATER_ENABLED);
        boolean update = updateObject == null || (boolean) updateObject;
        if(!update) return;
        Object delayObject = SettingsManager.getSetting(SettingType.UPDATER_DELAY);
        int configDelay = delayObject == null ? 18000 : (int) delayObject;
        startUpdaterScheduler(20, 20, configDelay);
    }

    private void startUpdaterScheduler(int startDelay, int delay, int configDelay) {
        updaterTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            int id = urlId.get();

            if(id == 1) getLogger().info("Switched to 'raw' page to check if the plugin is outdated.");
            else if(id > 1) {
                getLogger().warning("Failed reaching web host! (blocked by firewall? website down?)");
                Bukkit.getScheduler().cancelTask(updaterTaskId);
                return;
            }

            web.connect(path + urls[id]);
            String result = web.getResult();

            if(result.equals("unknown") || result.equals("exception")) urlId.addAndGet(1);
            else if(!getDescription().getVersion().equals(result)) {
                latestVersion = false;
                Bukkit.getScheduler().cancelTask(updaterTaskId);
                getLogger().warning("You're using an outdated version of this plugin!");
                getLogger().warning("Please update it on: https://www.rayzs.de/products/controlplayer/page");
            } else {
                Bukkit.getScheduler().cancelTask(updaterTaskId);
                startUpdaterScheduler(configDelay, configDelay, configDelay);
            }
        }, startDelay, delay);
    }
}