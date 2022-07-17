package de.rayzs.controlplayer.api.packetbased.animation;

import de.rayzs.controlplayer.api.packetbased.animation.impl.*;
import de.rayzs.controlplayer.api.version.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class ArmSwingAnimation {

    private static final ArmSwing armSwing;

   static {
       ServerVersion serverVersion = new ServerVersion(Bukkit.getServer());
       if(serverVersion.isModern()) armSwing = new ModernArmSwingAnimation(serverVersion.getRawVersionName());
       else armSwing = new LegacyArmSwingAnimation(serverVersion.getRawVersionName());
   }

    public void execute(Player player) {
        armSwing.execute(player);
    }
}