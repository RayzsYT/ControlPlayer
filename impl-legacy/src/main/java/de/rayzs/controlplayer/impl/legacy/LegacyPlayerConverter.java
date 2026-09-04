package de.rayzs.controlplayer.impl.legacy;

import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.api.player.PlayerConverter;
import de.rayzs.controlplayer.api.utils.SimplifiedLocation;
import de.rayzs.controlplayer.api.utils.SimplifiedPotionEffect;
import de.rayzs.controlplayer.api.utils.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class LegacyPlayerConverter implements PlayerConverter<Player> {

    private final String versionPackage;
    private boolean disableArmSwing = false, disableActionbar = false;

    public LegacyPlayerConverter() {
        versionPackage = VersionHelper.getVersionPackage();
    }

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
                if (VersionHelper.isAtMost(1, 12, 2)) {
                    return player.spigot().getCollidesWithEntities();
                }

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
            public void setAttribute(final String attributeKey, final double attributeValue) {}

            @Override
            public HashMap<String, Double> getAttributesMap() {
                return new HashMap<>();
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
                player.teleport(other.get());
            }

            @Override
            public void teleport(final SimplifiedLocation location) {
                player.teleport(new Location(
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
            public void setCollision(final boolean collision) {
                if (VersionHelper.isAtMost(1, 12, 2)) {
                    player.spigot().setCollidesWithEntities(collision);
                    return;
                }

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
                sendLegacyArmSwing(player);
            }

            @Override
            public void swingOffArm() {}

            @Override
            public void sendMessage(final String message) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('%', message));
            }

            @Override
            public void sendActionbar(final String message) {
                sendLegacyActionbar(player, ChatColor.translateAlternateColorCodes('%', message));
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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('%', message));
            }
        };
    }

    private void sendLegacyArmSwing(final Player player) {
        if (disableArmSwing) {
            return;
        }

        try {
            final Class<?> entityClass = Class.forName("net.minecraft.server." + versionPackage + ".Entity");
            final Object entityTargetPlayer = player.getClass().getMethod("getHandle").invoke(player);
            final Object animationPacket = Class.forName("net.minecraft.server." + versionPackage + ".PacketPlayOutAnimation").getDeclaredConstructor(entityClass, int.class).newInstance(entityTargetPlayer, 0);

            Bukkit.getOnlinePlayers().forEach(players -> {

                try {
                    final Object entityPlayer = players.getClass().getMethod("getHandle").invoke(players);
                    final Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

                    playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + versionPackage + ".Packet")).invoke(playerConnection, animationPacket);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

        } catch (Exception exception) {
            System.out.println("Arm-swing animations are not supported on this server software!");
            disableArmSwing = true;
        }
    }

    private void sendLegacyActionbar(final Player player, final String message) {
        if (disableActionbar) {
            return;
        }

        try {
            final Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            final Constructor<?> constructor = (Objects.<Class<?>>requireNonNull(Class.forName("net.minecraft.server." + versionPackage + ".PacketPlayOutChat"))).getConstructor(Class.forName("net.minecraft.server." + versionPackage + ".IChatBaseComponent"), byte.class);
            final Object iChatBaseComponent = (Objects.requireNonNull(Class.forName("net.minecraft.server." + versionPackage + ".IChatBaseComponent"))).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            final Object actionbarPacket = constructor.newInstance(iChatBaseComponent, (byte) 2);
            final Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + versionPackage + ".Packet")).invoke(playerConnection, actionbarPacket);

        } catch (Exception exception) {
            System.out.println("Actionbars are not supported on this server software!");
            disableActionbar = true;
        }
    }
}
