package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.hierarchy.HierarchyManager;
import de.rayzs.controlplayer.api.files.settings.*;
import de.rayzs.controlplayer.api.specific.SpecificControlManager;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import java.util.*;
import java.util.stream.Collectors;

public class ControlPlayerTabCompleter {

    public static List<String> getTabCompletion(CommandSender sender, String[] args) {
        List<String> results = new ArrayList<>();

        if(!(sender instanceof Player) || args.length > 1) return results;

        Player executor = (Player) sender;
        boolean specific = SpecificControlManager.doesPlayerHaveSpecificControlPerms(executor);


        if(sender.isOp() || sender.hasPermission("controlplayer.use") || specific)
            Bukkit.getOnlinePlayers().stream().filter(player -> {
                if (player.getName().equals(sender.getName())) return false;
                if (specific && !SpecificControlManager.canPlayerControl(executor, player))
                    return false;

                if (!(boolean) SettingsManager.getSetting(SettingType.SYSTEM_IGNOREBYPASS)) {
                    return !player.isOp() && (HierarchyManager.isHigher(executor, player) || (SpecificControlManager.canPlayerControl(executor, player)));
                }

                return true;
            }).forEach(player -> results.add(player.getName()));

        return results.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
    }
}
