package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.messages.*;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import de.rayzs.controlplayer.plugin.ControlPlayerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ControlPlayerReloadCommand extends Command {

    public ControlPlayerReloadCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if(!(sender.isOp() || sender.hasPermission("controlplayer.reload"))) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        sender.sendMessage(MessageManager.getMessage(MessageType.PREFIX) + " §6Reloading all files! Please wait...");
        MessageManager.reload(false);
        SettingsManager.reload(false);
        ControlManager.loadSettings();
        ControlPlayerPlugin.unregisterCommands();
        ControlPlayerPlugin.registerCommands();
        sender.sendMessage(MessageManager.getMessage(MessageType.PREFIX) + " §aDone!");

        return true;
    }
}
