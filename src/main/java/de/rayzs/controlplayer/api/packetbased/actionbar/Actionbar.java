package de.rayzs.controlplayer.api.packetbased.actionbar;

import de.rayzs.controlplayer.api.packetbased.actionbar.impl.*;
import de.rayzs.controlplayer.api.version.ServerVersion;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Actionbar {

    private final ServerVersion serverVersion;
    private final TextBar actionbar;

    public Actionbar(Server server) {
        serverVersion = new ServerVersion(server);
        if(serverVersion.isModern()) actionbar = new NewActionbar();
        else actionbar = new OldActionbar(serverVersion.getRawVersionName());
    }

    public void execute(Player player, String text) { actionbar.execute(player, text);
    }
}