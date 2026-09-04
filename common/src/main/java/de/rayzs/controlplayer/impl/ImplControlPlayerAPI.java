package de.rayzs.controlplayer.impl;

import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.config.ConfigProvider;
import de.rayzs.controlplayer.api.player.PlayerConverter;
import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.session.SessionProvider;
import de.rayzs.controlplayer.plugin.commands.*;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImplControlPlayerAPI implements ControlPlayerAPI<Player> {

    private final Logger logger;

    private final SessionProvider<Player> sessionProvider;
    private final PlayerConverter<Player> playerConverter;
    private final SchedulerProvider schedulerProvider;
    private final ConfigProvider configProvider;

    private final List<Command> registeredCommands = new ArrayList<>();
    private final CommandMap commandMap;

    public ImplControlPlayerAPI(
            final SessionProvider<Player> sessionProvider,
            final PlayerConverter<Player> playerConverter,
            final SchedulerProvider schedulerProvider,
            final ConfigProvider configProvider,
            final CommandMap commandMap,
            final Logger logger
    ) {
        this.sessionProvider = sessionProvider;
        this.playerConverter = playerConverter;
        this.schedulerProvider = schedulerProvider;
        this.configProvider = configProvider;

        this.commandMap = commandMap;
        this.logger = logger;
    }

    @Override
    public SessionProvider<Player> getSessionProvider() {
        return sessionProvider;
    }

    @Override
    public SchedulerProvider getSchedulerProvider() {
        return schedulerProvider;
    }

    @Override
    public PlayerConverter<Player> getPlayerConverter() {
        return playerConverter;
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    @Override
    public void reload() {
        registeredCommands.removeIf(command -> {
            command.unregister(commandMap);
            return true;
        });

        ConfigData.reload();
        registerCommands();
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    private void registerCommands() {
        registerCommand(ReloadCommand.class, ConfigData.Setting.COMMAND_ALIASES_RELOAD);
        registerCommand(FixCommand.class, ConfigData.Setting.COMMAND_ALIASES_FIX);

        registerCommand(ControlCommand.class, ConfigData.Setting.COMMAND_ALIASES_CONTROL);
        registerCommand(SemiControlCommand.class, ConfigData.Setting.COMMAND_ALIASES_SEMICONTROL);
        registerCommand(ForceControlCommand.class, ConfigData.Setting.COMMAND_ALIASES_FORCECONTROL);
        registerCommand(StopControlCommand.class, ConfigData.Setting.COMMAND_ALIASES_STOPCONTROL);
    }

    private void registerCommand(
            final Class<? extends Command> commandClass,
            final ConfigData.Setting commandAliasSetting
    ) {

        try {
            final List<String> commands = (List<String>) commandAliasSetting.getValue(List.class);
            if (commands == null || commands.isEmpty()) return;

            final String commandName = commands.get(0);
            final Command commandInstance = commandClass
                    .getConstructor(ControlPlayerAPI.class, List.class)
                    .newInstance(this, commands);

            registeredCommands.add(commandInstance);
            commandMap.register(commandName, commandInstance);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }

    }
}
