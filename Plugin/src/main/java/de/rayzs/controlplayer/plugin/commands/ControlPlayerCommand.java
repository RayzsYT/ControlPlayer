package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.api.files.messages.*;
import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
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
                if(args.length == 1) {
                    sendHelp = false;
                    Player victim = Bukkit.getPlayer(args[0]);
                    if (victim != null) {
                        int victimInstance = ControlManager.getInstanceState(victim);
                        if (victim != player) {
                            if ((boolean) SettingsManager.getSetting(SettingType.SYSTEM_IGNOREBYPASS) || !(victim.isOp() || victim.hasPermission("controlplayer.bypass"))) {
                                if (!victim.isDead()) {
                                    if (victimInstance != 1) {
                                        ControlState state = ControlManager.createControlInstance(player, victim, false);
                                        if (state == ControlState.SUCCESS)
                                            sender.sendMessage(MessageManager.getMessage(MessageType.NORMAL_SUCCESS).replace("%player%", victim.getName()));
                                        else sender.sendMessage(MessageManager.getMessage(MessageType.ERROR));
                                    } else sender.sendMessage(MessageManager.getMessage(MessageType.ALREADY_CONTROLLED));
                                } else sender.sendMessage(MessageManager.getMessage(MessageType.NOT_ALIVE));
                            } else sender.sendMessage(MessageManager.getMessage(MessageType.PLAYER_IMUN));
                        } else sender.sendMessage(MessageManager.getMessage(MessageType.SELF_CONTROL));
                    } else sender.sendMessage(MessageManager.getMessage(MessageType.NOT_ONLINE));
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
