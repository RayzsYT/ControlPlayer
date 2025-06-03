package de.rayzs.wearanything.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import java.util.List;

public interface WearArmor {
    String getName();
    ArmorEnum getArmorType();

    Material getArmorMaterial();
    String getArmorDisplayName();
    List<String> getArmorDescription();

    void playSound(Player player);

    boolean isSimilar(ItemStack stack);
}