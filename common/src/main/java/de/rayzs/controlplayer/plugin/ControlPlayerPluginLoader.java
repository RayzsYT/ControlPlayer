package de.rayzs.controlplayer.plugin;

import de.rayzs.controlplayer.api.ControlPlayer;
import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.config.ConfigProvider;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.api.player.PlayerConverter;
import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.scheduler.SchedulerTask;
import de.rayzs.controlplayer.api.session.SessionProvider;
import de.rayzs.controlplayer.api.utils.VersionComparer;
import de.rayzs.controlplayer.api.utils.VersionHelper;
import de.rayzs.controlplayer.impl.ImplConfigProvider;
import de.rayzs.controlplayer.impl.ImplControlPlayerAPI;
import de.rayzs.controlplayer.impl.ImplSessionProvider;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import de.rayzs.controlplayer.plugin.hook.PluginHooks;
import de.rayzs.controlplayer.plugin.listener.listeners.*;
import de.rayzs.controlplayer.plugin.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class ControlPlayerPluginLoader extends JavaPlugin {

    private SchedulerTask updaterTask;

    @Override
    public void onEnable() {
        VersionHelper.initialize(Bukkit.getBukkitVersion());
        new Metrics(this, 15651);


        final VersionHelper.Software software = VersionHelper.getSoftware();
        final String softwareName = getSoftwarePackageName(software);

        try {
            final PlayerConverter<Player> playerConverter = load(softwareName, PlayerConverter.class, JavaPlugin.class, (JavaPlugin) null);
            final SchedulerProvider schedulerProvider = load(softwareName, SchedulerProvider.class, JavaPlugin.class, (JavaPlugin) this);
            final SessionProvider<Player> sessionProvider = new ImplSessionProvider(schedulerProvider);
            final ConfigProvider configProvider = new ImplConfigProvider();

            final ControlPlayerAPI api = new ImplControlPlayerAPI(
                    sessionProvider,
                    playerConverter,
                    schedulerProvider,
                    configProvider,
                    fetchCommandMap(),
                    getLogger()
            );

            ControlPlayer.set(api);


            // Load default files
            ConfigProvider.exportResourceFileFile(getClass(), "config.yml", null);
            ConfigProvider.exportResourceFileFile(getClass(), "messages.yml", null);


            api.reload();
            startUpdaterTask(api);

        } catch (Exception exception) {
            exception.printStackTrace();
        }


        PluginHooks.values(); // To load all hooks. Nothing more.


        final PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new AdvancementListener(), this);
        manager.registerEvents(new BlockListener(), this);
        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(new EntityListener(), this);
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new PlayerListener(), this);
    }

    private String getSoftwarePackageName(final VersionHelper.Software software) {
        if (VersionHelper.isAtMost(1, 12, 2)) {
            return "Legacy";
        }

        if (software == VersionHelper.Software.FOLIA) {
            return "Folia";
        }

        if (software.isPaperBased()) {
            return "Paper";
        }

        return "Spigot";
    }

    private void startUpdaterTask(final ControlPlayerAPI api) {
        final CPSender sender = api.getPlayerConverter().convertSender(Bukkit.getConsoleSender());

        updaterTask = api.getSchedulerProvider().createAsyncScheduler(task -> {

            if (VersionComparer.get().computeComparison(sender,
                    ConfigData.Message.UPDATE_UPDATED_MESSAGE::send,
                    ConfigData.Message.UPDATE_OUTDATED_MESSAGE::send
            )) task.stop();

        }, 20L, 20L * 15);
    }


    private <T, P> T load(
            final String softwareName,
            final Class<T> targetClazz,
            final Class<?> parameterClazz,
            final P parameter
    ) throws Exception {

        final String name = targetClazz.getSimpleName();
        final Class<?> clazz = Class.forName(
                "de.rayzs.controlplayer.impl." + softwareName.toLowerCase() + "." + softwareName + name
        );

        if (parameter == null) {
            return (T) clazz.getConstructor().newInstance();
        }

        return (T) clazz.getConstructor(parameterClazz).newInstance(parameter);
    }

    private CommandMap fetchCommandMap() {
        try {
            final Class<? extends Server> clazz = Bukkit.getServer().getClass();
            final Field field = clazz.getDeclaredField("commandMap");

            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());

        } catch (Throwable throwable) {
            getLogger().warning("Could not access CommandMap!");
        }

        return null;
    }
}
