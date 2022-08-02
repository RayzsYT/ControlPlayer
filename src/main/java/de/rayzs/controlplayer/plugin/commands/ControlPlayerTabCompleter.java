package de.rayzs.controlplayer.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import java.util.*;

public class ControlPlayerTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if(sender.isOp() || sender.hasPermission("controlplayer.use"))
            Bukkit.getOnlinePlayers().stream().filter(player
                    -> !(player.isOp() || player.hasPermission("controlplayer.bypass"))).forEach(player
                    -> results.add(player.getName()));
        return results;
    }
}
