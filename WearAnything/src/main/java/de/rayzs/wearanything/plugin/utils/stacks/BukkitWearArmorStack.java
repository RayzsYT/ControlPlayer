package de.rayzs.wearanything.plugin.utils.stacks;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import de.rayzs.wearanything.plugin.WearAnything;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import de.rayzs.wearanything.api.*;
import org.bukkit.persistence.*;
import org.bukkit.entity.Player;
import org.bukkit.*;

import java.util.*;

public class BukkitWearArmorStack implements WearArmor, ConfigurationSerializable {

    private String name = null;
    private ArmorEnum armorType = null;

    private String displayName = null;
    private Material material = null;
    private List<String> lore = null, data = null;

    private Sound sound;
    private double volume = 0, pitch = 0;

    public BukkitWearArmorStack(String name, ArmorEnum armorType, String displayName, Material material, List<String> lore, List<String> data, Sound sound, double volume, double pitch) {
        this.name = name;
        this.armorType = armorType;

        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;

        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.data = data;
    }

    public BukkitWearArmorStack(String name, ArmorEnum armorType, ItemStack stack, Sound sound, double volume, double pitch) {
        this.name = name;
        this.armorType = armorType;

        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;

        this.material = stack.getType();

        if (!stack.hasItemMeta())
            return;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container != null) {
            data = new ArrayList<>();

            for (NamespacedKey key : container.getKeys()) {

                String keyValue = container.get(key, PersistentDataType.STRING);
                if (keyValue == null)
                    continue;

                data.add(key + "=" + keyValue);
            }
        }

        if (meta.hasDisplayName())
            this.displayName = meta.getDisplayName();

        if (meta.hasLore())
            this.lore = meta.getLore();
    }

    @Override
    public void playSound(Player player) {
        if (sound == null) {
            return;
        }

        player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
    }

    @Override
    public boolean isSimilar(ItemStack stack) {

        if (this.material != null && stack.getType() != this.material)
            return false;

        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (data != null && container != null) {

                for (NamespacedKey key : container.getKeys()) {
                    String keyValue = container.get(key, PersistentDataType.STRING);
                    if (keyValue == null)
                        continue;

                    if (!data.contains(key + "=" + keyValue))
                        return false;
                }
            }

            if (this.displayName != null) {
                if (! (meta.hasDisplayName() && meta.getDisplayName().equals(this.displayName)))
                    return false;
            }

            if (this.lore != null) {
                if (!(meta.hasLore() && this.lore.retainAll(meta.getLore()) && meta.getLore().retainAll(this.lore)))
                    return false;
            }

        } else {
            return displayName == null && lore == null;
        }

        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArmorEnum getArmorType() {
        return this.armorType;
    }

    @Override
    public Material getArmorMaterial() {
        return this.material;
    }

    @Override
    public String getArmorDisplayName() {
        return this.displayName;
    }

    @Override
    public List<String> getArmorDescription() {
        return this.lore;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("name", this.name);
        data.put("armor", this.armorType.name());

        data.put("sound-name", this.sound.name());
        data.put("sound-volume", this.volume == 0 ? null : this.pitch);
        data.put("sound-pitch", this.pitch == 0 ? null : this.pitch);

        data.put("item-material", this.material.name());
        data.put("item-displayName", this.displayName);
        data.put("item-lore", this.lore);
        data.put("item-data", this.data);

        return data;
    }

    public static BukkitWearArmorStack deserialize(java.util.Map<String, Object> data) {
        String name = (String) data.get("name");
        String armorTypeName = (String) data.get("armor");

        Sound sound = null;
        double soundVolume = 0, soundPitch = 0;

        if (data.get("sound-name") != null) {
            String soundName = (String) data.get("sound-name");
            soundVolume = (double) data.get("sound-volume");
            soundPitch = (double) data.get("sound-pitch");

            try {
                sound = Sound.valueOf(soundName);
            } catch (Exception exception) {
                WearAnything.getInstance().getLogger().warning("Invalid sound type (" + soundName + ") for " + name + "!");
                return null;
            }

        }

        String materialName = (String) data.get("item-material");
        String displayName = (String) data.get("item-displayName");
        List<String> lore = (List<String>) data.get("item-lore");
        List<String> dataList = (List<String>) data.get("item-data");

        Material material = null;

        try {
            material = Material.valueOf(materialName);
        } catch (Exception exception) {
            WearAnything.getInstance().getLogger().warning("Invalid material (" + armorTypeName + ") for " + name + "!");
            return null;
        }

        ArmorEnum armorType = null;

        try {
            armorType = ArmorEnum.valueOf(armorTypeName);
        } catch (Exception exception) {
            WearAnything.getInstance().getLogger().warning("Invalid armor type (" + armorTypeName + ") for " + name + "! (BOOT, LEGGING, CHESTPLATE, HELMET)");
            return null;
        }

        return new BukkitWearArmorStack(name, armorType, displayName, material, lore, dataList, sound, soundVolume, soundPitch);
    }
}