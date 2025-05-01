package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlState;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ControlPlayerStopCommand extends Command {

    public ControlPlayerStopCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender.isOp() || sender.hasPermission("controlplayer.stop"))) {
            sender.sendMessage(MessageManager.getMessage(MessageType.NO_PERMISSION));
            return true;
        }

        if (args.length < 1) {
            // SEND: Usage
            sender.sendMessage("/cps <controller>");
            return true;
        }

        Player controller = Bukkit.getPlayer(args[0]);

        if (controller == null) {
            // SEND: Controller not online
            sender.sendMessage("User offline.");
            return true;
        }

        int instanceId = ControlManager.getInstanceState(controller);

        if (instanceId == 0) {
            if (ControlManager.deleteControlInstance(controller)) {
                sender.sendMessage("Stopped control instance for controller(" + controller.getName() + ").");
            }

            return true;
        }

        sender.sendMessage("User is not controlling anyone.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> suggestions = Bukkit.getOnlinePlayers().stream().filter(player -> {
            int instanceId = ControlManager.getInstanceState(player);
            return instanceId == 0;
        }).map(Player::getName).collect(Collectors.toList());

        return suggestions.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
