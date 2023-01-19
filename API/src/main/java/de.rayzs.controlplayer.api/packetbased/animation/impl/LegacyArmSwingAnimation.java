package de.rayzs.controlplayer.api.packetbased.animation.impl;

import de.rayzs.controlplayer.api.packetbased.animation.ArmSwing;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class LegacyArmSwingAnimation implements ArmSwing {

    private final String versionName;
    private boolean disableArmSwing = false;

    public LegacyArmSwingAnimation(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public void execute(Player player) {
        if(disableArmSwing || player == null) return;
        try {
            Class<?> entityClass = Class.forName("net.minecraft.server." + versionName + ".Entity");
            Object entityTargetPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object animationPacket = Class.forName("net.minecraft.server." + versionName + ".PacketPlayOutAnimation").getDeclaredConstructor(entityClass, int.class).newInstance(entityTargetPlayer, 0);

            Bukkit.getOnlinePlayers().forEach(players -> {
                try {
                    Object entityPlayer = players.getClass().getMethod("getHandle").invoke(players);
                    Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                    playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + versionName + ".Packet")).invoke(playerConnection, animationPacket);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        }catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | InstantiationException exception) {
            System.err.println("Disabled ArmSwingAnimations because this version isn't is supported!");
            disableArmSwing = true;
        }
    }
}
