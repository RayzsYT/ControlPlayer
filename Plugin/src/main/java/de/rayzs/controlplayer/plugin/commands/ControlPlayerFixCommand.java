package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ControlPlayerFixCommand extends Command {

    public ControlPlayerFixCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if(!(sender.isOp() || sender.hasPermission("controlplayer.fix"))) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        String targetName = args.length > 0 ? args[0] : sender instanceof Player ? "-2" : "-1";

        if(targetName.equals("-1")) {
            sender.sendMessage(MessageManager.getMessage(MessageType.ONLY_PLAYERS));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName.equals("-2") ? sender.getName() : targetName);

        if(target == null) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NOT_ONLINE));
            return true;
        }

        if(ControlManager.getInstanceState(target) != -1) {
            if(targetName.equals("-2")) sender.sendMessage(MessageManager.getMessage(MessageType.PREFIX) + "§cYou are being controlled or controlling someone at the moment!");
            else sender.sendMessage(MessageManager.getMessage(MessageType.PREFIX) + "§cThis player is being controlled or is controlling someone at the moment!");
            return true;
        }

        target.setHealthScale(100);
        target.setHealth(20);

        ControlManager.fixPlayer(target);
        sender.sendMessage(MessageManager.getMessage(MessageType.PREFIX) + " §aFixed " + (targetName.equals("-2") ? "yourself" : target.getName()) + "!");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return ControlPlayerTabCompleter.getTabCompletion(sender, args);
    }
}
