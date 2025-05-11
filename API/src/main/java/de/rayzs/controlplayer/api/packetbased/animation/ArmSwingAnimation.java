package de.rayzs.controlplayer.api.packetbased.animation;

import de.rayzs.controlplayer.api.packetbased.animation.impl.LegacyArmSwingAnimation;
import de.rayzs.controlplayer.api.packetbased.animation.impl.ModernArmSwingAnimation;
import de.rayzs.controlplayer.api.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArmSwingAnimation {

    private static final ArmSwing armSwing;

   static {
       ServerVersion serverVersion = new ServerVersion(Bukkit.getServer());
       if(serverVersion.isModern()) armSwing = new ModernArmSwingAnimation(serverVersion.getRawVersionName());
       else armSwing = new LegacyArmSwingAnimation(serverVersion.getRawVersionName());
   }

    public void execute(Player player, String animationTypeAsString) {
        armSwing.execute(player, animationTypeAsString);
    }
}