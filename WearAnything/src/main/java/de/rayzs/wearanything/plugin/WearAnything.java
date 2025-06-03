package de.rayzs.wearanything.plugin;

import de.rayzs.wearanything.api.configuration.ConfigurationBuilder;
import de.rayzs.wearanything.api.listener.PlayerEquipEvent;
import org.bukkit.inventory.ItemStack;
import de.rayzs.wearanything.api.*;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import org.bukkit.Sound;
import java.util.*;
import java.util.stream.Collectors;

public class WearAnything {

    private static final WearAnything instance = new WearAnything();

    private WearAnything() {}

    public static WearAnything getInstance() {
        return instance;
    }

    private Logger logger;
    private WearArmorConverter converter;
    private ConfigurationBuilder configuration;
    private Map<WearArmor, ArmorEnum> cachedArmor = new HashMap<>();

    private Sound defaultSound = null;
    private double defaultSoundVolume = 1.0;
    private double defaultSoundPitch = 1.0;

    public void initialize(Logger logger, WearArmorConverter converter, ConfigurationBuilder configuration) {
        this.logger = logger;
        this.configuration = configuration;
        this.converter = converter;

        loadAll();
    }

    public Logger getLogger() {
        return logger;
    }

    public void loadAll() {
        cachedArmor = new HashMap<>();
        long startTime = System.currentTimeMillis();

        logger.info("Loading default sound...");

        String soundName = (String) configuration.getOrSet("default-sound.sound", Sound.ITEM_ARMOR_EQUIP_GENERIC.name());
        try {
            defaultSound = Sound.valueOf(soundName);
            defaultSoundVolume = (double) configuration.getOrSet("default-sound.volume", 1.0);;
            defaultSoundPitch = (double) configuration.getOrSet("default-sound.pitch", 1.0);;
        } catch (Exception exception) {
            logger.warning("Default sound " + soundName + " does not exist!");
        }

        if (configuration.get("items") != null)
            for (String key : configuration.getKeys("items", false)) {
                logger.info("Loading armor " + key + "...");

                try {
                    WearArmor wearArmor = converter.get(key);
                    cachedArmor.put(wearArmor, wearArmor.getArmorType());
                } catch (Exception exception) {
                    logger.warning("Failed to load armor " + key + ": " + exception.getMessage());
                }
            }

        logger.info("Loaded " + cachedArmor.size() + " in total! (" + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public WearArmorConverter getConverter() {
        return this.converter;
    }

    public ConfigurationBuilder getConfiguration() {
        return configuration;
    }

    public WearArmor getArmorFromStack(ItemStack stack) {
        Optional<Map.Entry<WearArmor, ArmorEnum>> optional = cachedArmor.entrySet().stream().filter(entry -> entry.getKey().isSimilar(stack)).findFirst();
        return optional.map(Map.Entry::getKey).orElse(null);
    }

    public void reload() {
        WearAnything.getInstance().getConfiguration().reload();
    }

    public void save(WearArmor wearArmor) {
        WearAnything.getInstance().getConfiguration().setAndSave("items." + wearArmor.getName(), wearArmor);
        cachedArmor.put(wearArmor, wearArmor.getArmorType());
    }

    public void remove(String name) {
        WearArmor wearArmor = converter.get(name);
        WearAnything.getInstance().getConfiguration().setAndSave("items." + name, null);
        cachedArmor.remove(wearArmor);
    }

    public boolean exist(String name) {
        return WearAnything.getInstance().getConfiguration().get("items." + name) != null;
    }

    public boolean isValid(ItemStack stack) {
        return WearAnything.getInstance().getArmorFromStack(stack) != null;
    }

    public boolean isValidSlot(int slot) {
        return slot >= ArmorEnum.BOOT.get() && slot <= ArmorEnum.HELMET.get();
    }

    public ArmorEnum getArmorTypeBySlot(int slot) {
        if (!isValidSlot(slot))
            return null;

        for (ArmorEnum value : ArmorEnum.values()) {
            if (slot != value.get())
                continue;

            return value;
        }

        return null;
    }

    public boolean handleEquip(Player player, WearArmor wearArmor, ItemStack stack) {
        PlayerEquipEvent event = new PlayerEquipEvent(player, wearArmor, stack);

        if (event.isCancelled())
            return false;

        if (stack != null) {
            wearArmor.playSound(player);
        }

        return true;
    }

    public List<String> getArmorNames() {
        return cachedArmor.isEmpty() ? new ArrayList<>() : cachedArmor.keySet().stream().map(WearArmor::getName).toList();
    }

    public Sound getDefaultSound() {
        return defaultSound;
    }

    public double getDefaultSoundVolume() {
        return defaultSoundVolume;
    }

    public double getDefaultSoundPitch() {
        return defaultSoundPitch;
    }
}
