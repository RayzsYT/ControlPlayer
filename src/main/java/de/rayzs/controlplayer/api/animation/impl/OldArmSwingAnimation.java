package de.rayzs.controlplayer.api.animation.impl;

import de.rayzs.controlplayer.api.animation.ArmSwing;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;

public class OldArmSwingAnimation implements ArmSwing {

    private final String versionName;
    private boolean disableArmSwing = false;

    public OldArmSwingAnimation(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public void execute(Player player) {
            if(disableArmSwing || player == null) return;
            try {
                Class<?> entityClass = Class.forName("net.minecraft.server." + versionName + ".Entity");
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object animationPacket = Class.forName("net.minecraft.server." + versionName + ".PacketPlayOutAnimation").getDeclaredConstructor(entityClass, int.class).newInstance(entityPlayer, 0);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + versionName + ".Packet")).invoke(playerConnection, animationPacket);
            }catch (ClassNotFoundException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchFieldException
                    | InstantiationException exception) {
                System.out.println("Disabled ArmSwingAnimations because this version isn't is supported!");
                disableArmSwing = true;
        }
    }
}
