package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlState;
import de.rayzs.controlplayer.api.files.messages.MessageManager;
import de.rayzs.controlplayer.api.files.messages.MessageType;
import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import de.rayzs.controlplayer.api.hierarchy.HierarchyManager;
import de.rayzs.controlplayer.api.specific.SpecificControlManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            sender.sendMessage("/cpo <controller> <victim>");
            return true;
        }

        Player controller = Bukkit.getPlayer(args[0]), victim = Bukkit.getPlayer(args[1]);

        if (controller == null) {
            // SEND: Controller not online
            sender.sendMessage("Controller offline.");
            return true;
        }

        int instanceId = ControlManager.getInstanceState(controller);

        if (instanceId == 0) {

            if (ControlManager.deleteControlInstance(controller)) {
                sender.sendMessage("Stopped control instance for controller(" + controller.getName() + ").");
            }

            return true;
        }

        if (victim == null) {
            // SEND: Victim not online
            sender.sendMessage("Victim offline.");
            return true;
        }

        if (controller == victim) {
            // SEND: Cannot control controller
            sender.sendMessage("Controller cannot be victim.");
            return true;
        }

        if (controller.isDead()) {
            // SEND: Controller not alive
            sender.sendMessage("Controller dead.");
            return true;
        }

        if (victim.isDead()) {
            // SEND: Victim not alive
            sender.sendMessage("Victim dead.");
            return true;
        }


        int victimInstanceId = ControlManager.getInstanceState(victim);

        if (instanceId == 1) {
            // SEND: Controller already being controlled
            sender.sendMessage("Controller already being controlled");
            return true;
        }

        if (victimInstanceId == 1) {
            // SEND: Victim already being controlled
            sender.sendMessage("Victim already being controlled");
            return true;
        }

        ControlState state = ControlManager.createControlInstance(controller, victim, false);
        if (state != ControlState.SUCCESS)
            return true;

        sender.sendMessage("Done! Controller(" + controller.getName() + ") is now controlling victim(" + victim.getName() + ").");
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
