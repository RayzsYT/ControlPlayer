package de.rayzs.controlplayer.api.packetbased.actionbar.impl;

import de.rayzs.controlplayer.api.packetbased.actionbar.TextBar;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class OldActionbar implements TextBar {

    private final String versionName;
    private boolean disableArmSwing = false;

    public OldActionbar(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public void execute(Player player, String text) {
        if(disableArmSwing || player == null) return;
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Constructor<?> constructor = (Objects.<Class<?>>requireNonNull(Class.forName("net.minecraft.server." + versionName + ".PacketPlayOutChat"))).getConstructor(Class.forName("net.minecraft.server." + versionName + ".IChatBaseComponent"), byte.class);
            Object iChatBaseComponent = (Objects.requireNonNull(Class.forName("net.minecraft.server." + versionName + ".IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + text + "\"}");
            Object actionbarPacket = constructor.newInstance(iChatBaseComponent, (byte) 2);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + versionName + ".Packet")).invoke(playerConnection, actionbarPacket);
        }catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException
                | InstantiationException exception) {
            System.out.println("Disabled Actionbar because this version isn't is supported!");
            disableArmSwing = true;
        }
    }
}
