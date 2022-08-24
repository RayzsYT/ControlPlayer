package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.*;
import de.rayzs.controlplayer.api.files.messages.*;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ControlPlayerCommand extends MessageManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean sendHelp = true;

        if(!(sender instanceof Player)) {
            sender.sendMessage(getMessage(MessageType.ONLY_PLAYERS));
            return true;
        }

        if(!(sender.isOp() || sender.hasPermission("controlplayer.use"))) {
            sender.sendMessage(getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        Player player = (Player) sender;
        int playerInstance = ControlManager.getInstanceState(player);

        switch (playerInstance) {
            case 0:
                sender.sendMessage(ControlManager.deleteControlInstance(player) ? getMessage(MessageType.STOPPED_CONTROLLING) : getMessage(MessageType.ERROR));
                return true;
            case 1: default:
                sender.sendMessage(getMessage(MessageType.BEING_CONTROLLED));
                return true;
            case -1:
                if(args.length == 1) {
                    sendHelp = false;
                    Player victim = Bukkit.getPlayer(args[0]);
                    if (victim != null) {
                        int victimInstance = ControlManager.getInstanceState(victim);
                        if (victim != player) {
                            if (!(victim.isOp() || victim.hasPermission("controlplayer.bypass"))) {
                                if (!victim.isDead()) {
                                    if (victimInstance != 1) {
                                        ControlState state = ControlManager.createControlInstance(player, victim);
                                        if (state == ControlState.SUCCESS)
                                            sender.sendMessage(getMessage(MessageType.SUCCESS).replace("%player%", victim.getName()));
                                        else sender.sendMessage(getMessage(MessageType.ERROR));
                                    } else sender.sendMessage(getMessage(MessageType.ALREADY_CONTROLLED));
                                } else sender.sendMessage(getMessage(MessageType.NOT_ALIVE));
                            } else sender.sendMessage(getMessage(MessageType.PLAYER_IMUN));
                        } else sender.sendMessage(getMessage(MessageType.SELF_CONTROL));
                    } else sender.sendMessage(getMessage(MessageType.NOT_ONLINE));
                    break;
                }
        }

        if(sendHelp) sender.sendMessage(getMessage(MessageType.USAGE));
        return true;
    }
}
