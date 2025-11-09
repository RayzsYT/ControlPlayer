package de.rayzs.controlplayer.api.packetbased.actionbar.impl;

import de.rayzs.controlplayer.api.packetbased.actionbar.TextBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class NewActionbar implements TextBar {

    @Override
    public void execute(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
