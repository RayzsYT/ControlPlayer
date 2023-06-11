package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.files.settings.SettingType;
import de.rayzs.controlplayer.api.files.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import java.util.*;

public class ControlPlayerTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if(sender.isOp() || sender.hasPermission("controlplayer.use"))
            Bukkit.getOnlinePlayers().stream().filter(player
                    -> {
                if(player.getName().equals(sender.getName())) return false;
                return (boolean) SettingsManager.getSetting(SettingType.SYSTEM_IGNOREBYPASS) || !(player.isOp() || player.hasPermission("controlplayer.bypass"));
            }).forEach(player
                    -> results.add(player.getName()));
        return results;
    }
}
