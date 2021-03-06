package de.rayzs.controlplayer.api.animation.impl;

import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import de.rayzs.controlplayer.api.animation.ArmSwing;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.entity.Player;

public class NewArmSwingAnimation implements ArmSwing {

    @Override
    public void execute(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        entityPlayer.b.sendPacket(new PacketPlayOutAnimation(entityPlayer, 0));
    }
}
