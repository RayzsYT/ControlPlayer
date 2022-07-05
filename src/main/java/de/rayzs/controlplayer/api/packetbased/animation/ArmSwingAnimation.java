package de.rayzs.controlplayer.api.packetbased.animation;

import de.rayzs.controlplayer.api.packetbased.animation.impl.*;
import de.rayzs.controlplayer.api.version.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.Server;

public class ArmSwingAnimation {

    private final ServerVersion serverVersion;
    private final ArmSwing armSwing;

    public ArmSwingAnimation(Server server) {
        serverVersion = new ServerVersion(server);
        if(serverVersion.isModern()) armSwing = new ModernArmSwingAnimation();
        else armSwing = new LegacyArmSwingAnimation(serverVersion.getRawVersionName());
    }

    public void execute(Player player) {
        armSwing.execute(player);
    }
}