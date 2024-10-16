package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.hierarchy.HierarchyManager;
import de.rayzs.controlplayer.api.files.settings.*;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.Bukkit;
import java.util.*;

public class ControlPlayerTabCompleter {

    public static List<String> getTabCompletion(CommandSender sender) {
        List<String> results = new ArrayList<>();

        if(!(sender instanceof Player)) return results;
        Player executor = (Player) sender;

        if(sender.isOp() || sender.hasPermission("controlplayer.use"))
            Bukkit.getOnlinePlayers().stream().filter(player -> {
                if(player.getName().equals(sender.getName())) return false;

                if(!(boolean) SettingsManager.getSetting(SettingType.SYSTEM_IGNOREBYPASS)) {
                    if(player.isOp() || HierarchyManager.isHigher(player, executor))
                        return false;
                }

                return true;
            }).forEach(player
                    -> results.add(player.getName()));
        return results;
    }
}
