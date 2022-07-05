package de.rayzs.controlplayer.plugin;

import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.plugin.commands.*;
import de.rayzs.controlplayer.plugin.events.*;
import de.rayzs.controlplayer.api.web.WebConnection;
import de.rayzs.controlplayer.plugin.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

public class ControlPlayerPlugin extends JavaPlugin {

    private static ControlPlayerPlugin instance;

    private boolean latestVersion = true;
    private final WebConnection web = new WebConnection();
    private final Class<?>[] listeners = {AsyncPlayerChat.class, PlayerInteraction.class, PlayerChangeWorld.class, PlayerJoin.class, PlayerQuit.class, PlayerDeath.class};
    private final String[] commandNames = {"cp", "controlplayer", "cplayer", "controlp"};
    private int updaterTaskId;

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
        for (Class<?> clazz : listeners) try {
            Listener listener = (Listener) clazz.newInstance();
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (Exception exception) { exception.printStackTrace(); }
    }

    protected void registerCommands() {
        ControlPlayerCommand commandClass = new ControlPlayerCommand();
        ControlPlayerTabCompleter tabCompleterClass = new ControlPlayerTabCompleter();
        for (String commandName : commandNames) {
            PluginCommand command = getCommand(commandName);
            command.setExecutor(commandClass);
            command.setTabCompleter(tabCompleterClass);
        }
    }

    protected void startUpdaterTask() {
        Object updateObject = SettingsManager.getSetting(SettingType.UPDATER_ENABLED);
        boolean update = updateObject == null || (boolean) updateObject;
        if(!update) return;
        Object delayObject = SettingsManager.getSetting(SettingType.UPDATER_DELAY);
        int delay = delayObject == null ? 18000 : (int) delayObject;
        updaterTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if(!web.connect("https://www.rayzs.de/products/controlplayer/version/index.php").getResult().equals(getDescription().getVersion())) {
                Bukkit.getScheduler().cancelTask(updaterTaskId);
                getLogger().warning("You're using an outdated version of this plugin!");
                getLogger().warning("Please update it on: https://www.rayzs.de/products/controlplayer/page");
                latestVersion = false;
            }
        }, 20, delay);
    }
}