package de.rayzs.controlplayer.api.packetbased.actionbar;

import de.rayzs.controlplayer.api.packetbased.actionbar.impl.*;
import de.rayzs.controlplayer.api.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Actionbar {

    private static final TextBar actionbar;

    static {
        ServerVersion serverVersion = new ServerVersion(Bukkit.getServer());
        if(serverVersion.isModern()) actionbar = new NewActionbar();
        else actionbar = new OldActionbar(serverVersion.getRawVersionName());
    }

    public void execute(Player player, String text) { actionbar.execute(player, text);
    }
}