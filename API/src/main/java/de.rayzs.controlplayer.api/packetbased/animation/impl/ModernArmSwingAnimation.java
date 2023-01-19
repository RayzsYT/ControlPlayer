package de.rayzs.controlplayer.api.packetbased.animation.impl;

import de.rayzs.controlplayer.api.packetbased.animation.ArmSwing;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ModernArmSwingAnimation implements ArmSwing {

    private final String versionName;
    private boolean disableArmSwing = false;

    public ModernArmSwingAnimation(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public void execute(Player player) {
        if (disableArmSwing || player == null) return;
        boolean is117 = versionName.contains("17");
        String sendPacketMethodeName = is117 ? "sendPacket" : "a",
                packetClassPath = is117 ? "net.minecraft.network.protocol.game.Packet" : "net.minecraft.network.protocol.Packet";

        try {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Object entityTargetPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object animationPacket = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutAnimation").getDeclaredConstructor(entityClass, int.class).newInstance(entityTargetPlayer, 0);

            Bukkit.getOnlinePlayers().forEach(players -> {
                try {
                    Object entityPlayer = players.getClass().getMethod("getHandle").invoke(players);
                    Object playerConnection = entityPlayer.getClass().getField("b").get(entityPlayer);
                    playerConnection.getClass().getMethod(sendPacketMethodeName, Class.forName(packetClassPath)).invoke(playerConnection, animationPacket);
                }catch (Exception exception) { exception.printStackTrace(); }
            });
           } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | InstantiationException exception) {
            System.err.println("Disabled ArmSwingAnimations because this version isn't is supported!");
            disableArmSwing = true;
        }
    }
}
