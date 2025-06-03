package de.rayzs.wearanything.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Sound;

public interface WearArmorConverter {

    WearArmor get(String name);
    WearArmor convert(String name, ArmorEnum armorType, ItemStack stack, Sound sound, double volume, double pitch);
}
