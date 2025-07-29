package de.rayzs.controlplayer.plugin.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlState;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;

public class ControlPlayerOtherCommand extends Command {

    public ControlPlayerOtherCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender.isOp() || sender.hasPermission("controlplayer.other"))) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        if (args.length < 2) {
            // SEND: Usage
            
            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_USAGE)
            );

            return true;
        }

        Player controller = Bukkit.getPlayer(args[0]), victim = Bukkit.getPlayer(args[1]);

        if (controller == null) {
            // SEND: Controller not online
            
            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_OFFLINE)
                .replace("%player%", args[0])
            );

            return true;
        }

        int instanceId = ControlManager.getInstanceState(controller);

        if (instanceId == 0) {

            if (ControlManager.deleteControlInstance(controller)) {
                
                sender.sendMessage(
                    MessageManager.getMessage(MessageType.OTHER_STOPPED)
                    .replace("%player%", controller.getName())
            );

            }

            return true;
        }

        if (victim == null) {
            // SEND: Victim not online

            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_OFFLINE)
                .replace("%player%", args[1])
            );

            return true;
        }

        if (controller == victim) {
            // SEND: Cannot control controller

            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_SAME)
                .replace("%player%", args[0])
            );

            return true;
        }

        if (controller.isDead()) {
            // SEND: Controller not alive

            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_DEAD)
                .replace("%player%", controller.getName())
            );

            return true;
        }

        if (victim.isDead()) {
            // SEND: Victim not alive

            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_DEAD)
                .replace("%player%", victim.getName())
            );

            return true;
        }


        int victimInstanceId = ControlManager.getInstanceState(victim);

        if (instanceId == 1) {
            // SEND: Controller already being controlled

            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_ALREADY_CONTROLLED)
                .replace("%player%", controller.getName())
            );

            return true;
        }

        if (victimInstanceId == 1) {
            // SEND: Victim already being controlled
 
            sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_ALREADY_CONTROLLED)
                .replace("%player%", victim.getName())
            );

            return true;
        }

        ControlState state = ControlManager.createControlInstance(controller, victim, false);
        if (state != ControlState.SUCCESS)
            return true;

        sender.sendMessage(
                MessageManager.getMessage(MessageType.OTHER_SUCCESS)
                        .replace("%player%", controller.getName())
                        .replace("%victim%", victim.getName())
        );

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> suggestions = Bukkit.getOnlinePlayers().stream().filter(player -> {
            int instanceId = ControlManager.getInstanceState(player);
            return instanceId == -1;
        }).map(Player::getName).collect(Collectors.toList());

        if (args.length >= 1)
            suggestions.remove(args[0]);

        return suggestions.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
