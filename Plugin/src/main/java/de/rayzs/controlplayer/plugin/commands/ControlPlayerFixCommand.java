package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ControlPlayerFixCommand extends MessageManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender.isOp() || sender.hasPermission("controlplayer.fix"))) {
            sender.sendMessage(getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        String targetName = args.length > 0 ? args[0] : sender instanceof Player ? "-2" : "-1";

        if(targetName.equals("-1")) {
            sender.sendMessage(getMessage(MessageType.ONLY_PLAYERS));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName.equals("-2") ? sender.getName() : targetName);

        if(target == null) {
            sender.sendMessage(getMessage(MessageType.NOT_ONLINE));
            return true;
        }

        if(ControlManager.getInstanceState(target) != -1) {
            if(targetName.equals("-2")) sender.sendMessage(getMessage(MessageType.PREFIX) + "§cYou are being controlled or controlling someone at the moment!");
            else sender.sendMessage(getMessage(MessageType.PREFIX) + "§cThis player is being controlled or is controlling someone at the moment!");
            return true;
        }

        ControlManager.fixPlayer(target);
        sender.sendMessage(getMessage(MessageType.PREFIX) + " §aFixed " + (targetName.equals("-2") ? "yourself" : target.getName()) + "!");

        return true;
    }
}
