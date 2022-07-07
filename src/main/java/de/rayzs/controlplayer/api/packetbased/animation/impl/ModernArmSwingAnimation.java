package de.rayzs.controlplayer.api.packetbased.animation.impl;

import de.rayzs.controlplayer.api.packetbased.animation.ArmSwing;
import de.rayzs.controlplayer.api.version.ServerVersion;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
        try {
            Class<?> entityClass = Class.forName("net.minecraft.world.entity.Entity");
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object animationPacket = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutAnimation").getDeclaredConstructor(entityClass, int.class).newInstance(entityPlayer, 0);
            Object playerConnection = entityPlayer.getClass().getField("b").get(entityPlayer);
            if(versionName.contains("17")) playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.network.protocol.game.Packet")).invoke(playerConnection, animationPacket);
            else playerConnection.getClass().getMethod("a", Class.forName("net.minecraft.network.protocol.Packet")).invoke(playerConnection, animationPacket);
        } catch (ClassNotFoundException
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
