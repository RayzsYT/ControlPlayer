package de.rayzs.wearanything.plugin.utils;

import de.rayzs.wearanything.plugin.utils.stacks.BukkitWearArmorStack;
import de.rayzs.wearanything.plugin.WearAnything;
import org.bukkit.inventory.ItemStack;
import de.rayzs.wearanything.api.*;
import org.bukkit.*;

public class BukkitWearArmorConverter implements WearArmorConverter {

    private static final BukkitWearArmorConverter instance = new BukkitWearArmorConverter();

    public static BukkitWearArmorConverter getInstance() {
        return instance;
    }

    private BukkitWearArmorConverter() {}

    @Override
    public WearArmor get(String name) {
        return (WearArmor) WearAnything.getInstance().getConfiguration().get("items." + name);
    }

    @Override
    public WearArmor convert(String name, ArmorEnum armorType, ItemStack stack, Sound sound, double volume, double pitch) {
        return new BukkitWearArmorStack(name, armorType, stack, sound, volume, pitch);
    }
}