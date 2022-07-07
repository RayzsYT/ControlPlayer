package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlState;
import de.rayzs.controlplayer.api.files.message.MessageManager;
import de.rayzs.controlplayer.api.files.message.MessageType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ControlPlayerReloadCommand extends MessageManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender.isOp() || sender.hasPermission("controlplayer.reload"))) {
            sender.sendMessage(getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        sender.sendMessage(getMessage(MessageType.PREFIX) + " §6Reloading all files! Please wait...");
        MessageManager.reload(false);
        SettingsManager.reload(false);
        sender.sendMessage(getMessage(MessageType.PREFIX) + " §aDone!");

        return true;
    }
}
