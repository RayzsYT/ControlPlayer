package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.api.files.messages.*;
import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import de.rayzs.controlplayer.api.hierarchy.HierarchyManager;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;

public class ControlPlayerCommand extends Command {

    public ControlPlayerCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean sendHelp = true;

        if(!(sender instanceof Player)) {
            sender.sendMessage(MessageManager.getMessage(MessageType.ONLY_PLAYERS));
            return true;
        }

        if(!(sender.isOp() || sender.hasPermission("controlplayer.use"))) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        Player player = (Player) sender;
        int playerInstance = ControlManager.getInstanceState(player);

        switch (playerInstance) {
            case 0:
                sender.sendMessage(ControlManager.deleteControlInstance(player) ? MessageManager.getMessage(MessageType.STOPPED_CONTROLLING) : MessageManager.getMessage(MessageType.ERROR));
                return true;
            case 1: default:
                sender.sendMessage(MessageManager.getMessage(MessageType.BEING_CONTROLLED));
                return true;
            case -1:
                if (args.length == 1) {
                    sendHelp = false;
                    Player victim = Bukkit.getPlayer(args[0]);

                    if (victim == null) {
                        sender.sendMessage(MessageManager.getMessage(MessageType.NOT_ONLINE));
                        return true;
                    }

                    int victimInstance = ControlManager.getInstanceState(victim);
                    if (victim == player) {
                        sender.sendMessage(MessageManager.getMessage(MessageType.SELF_CONTROL));
                        sender.sendMessage(MessageManager.getMessage(MessageType.SELF_CONTROL));
                        return true;
                    }

                    if(!(boolean) SettingsManager.getSetting(SettingType.SYSTEM_IGNOREBYPASS)) {
                        if(victim.isOp() || HierarchyManager.isHigher(player, victim)) {
                            sender.sendMessage(MessageManager.getMessage(MessageType.PLAYER_IMUN));
                            return true;
                        }
                    }

                    if (victim.isDead()) {
                        sender.sendMessage(MessageManager.getMessage(MessageType.NOT_ALIVE));
                        return true;
                    }

                    if (victimInstance == 1) {
                        sender.sendMessage(MessageManager.getMessage(MessageType.ALREADY_CONTROLLED));
                        return true;
                    }

                    ControlState state = ControlManager.createControlInstance(player, victim, false);
                    if (state == ControlState.SUCCESS)
                        sender.sendMessage(MessageManager.getMessage(MessageType.NORMAL_SUCCESS).replace("%player%", victim.getName()));

                    break;
                }
        }

        if(sendHelp) sender.sendMessage(MessageManager.getMessage(MessageType.NORMAL_USAGE));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return ControlPlayerTabCompleter.getTabCompletion(sender);
    }
}
