package de.rayzs.controlplayer.impl.folia;

import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.api.player.PlayerConverter;
import de.rayzs.controlplayer.api.utils.SimplifiedLocation;
import de.rayzs.controlplayer.api.utils.SimplifiedPotionEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class FoliaPlayerConverter implements PlayerConverter<Player> {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public CPPlayer<Player> convertPlayer(final Player player) {
        return new CPPlayer<>() {
            @Override
            public Player get() {
                return player;
            }

            @Override
            public double getHealth() {
                return player.getHealth();
            }

            @Override
            public double getHealthScale() {
                return player.getHealthScale();
            }

            @Override
            public boolean canCollide() {
                return player.isCollidable();
            }

            @Override
            public boolean isOnline() {
                return player.isOnline();
            }

            @Override
            public String getName() {
                return player.getName();
            }

            @Override
            public UUID getUUID() {
                return player.getUniqueId();
            }

            @Override
            public void setAttribute(final String attributeKey, final double attributeValue) {
                try {
                    final Attribute attribute = Attribute.valueOf(attributeKey);
                    player.getAttribute(attribute).setBaseValue(attributeValue);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public HashMap<String, Double> getAttributesMap() {
                final HashMap<String, Double> map = new HashMap<>();

                for (final Attribute attribute : Attribute.values()) {
                    final AttributeInstance instance = player.getAttribute(attribute);
                    if (instance == null) continue;

                    final double value = instance.getBaseValue();
                    map.put(attribute.name(), value);
                }

                return map;
            }

            @Override
            public void removePotionEffect(final String effectType) {
                player.removePotionEffect(PotionEffectType.getByName(effectType));
            }

            @Override
            public HashSet<SimplifiedPotionEffect> getActivePotionEffects() {
                return new HashSet<>(player.getActivePotionEffects().stream().map(potion ->
                        new SimplifiedPotionEffect(
                                potion.getType().getName(),
                                potion.getDuration(),
                                potion.getAmplifier())
                ).toList());
            }

            @Override
            public void addPotionEffect(final SimplifiedPotionEffect effect) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.getByName(effect.effectType()),
                        effect.duration(),
                        effect.amplifier()
                ));
            }

            @Override
            public void teleport(final CPPlayer<Player> other) {
                player.teleportAsync(other.get().getLocation());
            }

            @Override
            public void teleport(final SimplifiedLocation location) {
                player.teleportAsync(new Location(
                        Bukkit.getWorld(location.worldName()),
                        location.x(),
                        location.y(),
                        location.z(),
                        location.yaw(),
                        location.pitch()
                ));
            }

            @Override
            public boolean isSame(final CPPlayer<Player> other) {
                return other.getUUID().equals(getUUID());
            }

            @Override
            public void setCollision(boolean collision) {
                player.setCollidable(collision);
            }

            @Override
            public void setHealth(double health) {
                player.setHealth(health);
            }

            @Override
            public void setHealthScale(double healthScale) {
                player.setHealthScale(healthScale);
            }

            @Override
            public void swingMainArm() {
                player.swingMainHand();
            }

            @Override
            public void swingOffArm() {
                player.swingOffHand();
            }

            @Override
            public void sendMessage(final String message) {
                player.sendMessage(modify(message));
            }

            @Override
            public void sendActionbar(final String message) {
                player.sendActionBar(modify(message));
            }
        };
    }

    @Override
    public CPSender convertSender(final Object senderObj) {
        if (! (senderObj instanceof CommandSender sender)) {
            throw new IllegalArgumentException("Invalid object type! Sender must be of type CommandSender.");
        }

        return new CPSender() {
            @Override
            public boolean isConsole() {
                return true;
            }

            @Override
            public void sendMessage(final String message) {
                sender.sendMessage(modify(message));
            }
        };
    }

    private Component modify(String text) {
        text = text.replace("§", "&");

        final Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        text = miniMessage.serialize(legacy).replace("\\", "");

        return miniMessage.deserialize(text);
    }
}
