package de.rayzs.controlplayer.api.player;

import de.rayzs.controlplayer.api.utils.SimplifiedLocation;
import de.rayzs.controlplayer.api.utils.SimplifiedPotionEffect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public interface CPPlayer<P> extends CPSender {
    P get();

    @Override
    default boolean isConsole() {
        return false;
    }

    default boolean isPlayer() {
        return true;
    }

    void swingMainArm();
    void swingOffArm();

    void setHealth(double health);
    void setHealthScale(double healthScale);

    void setCollision(boolean collision);

    double getHealth();
    double getHealthScale();

    boolean canCollide();
    boolean isOnline();
    boolean isSame(final CPPlayer<P> other);

    void removePotionEffect(final String effectType);
    void addPotionEffect(final SimplifiedPotionEffect effect);
    HashSet<SimplifiedPotionEffect> getActivePotionEffects();

    void setAttribute(final String attributeKey, final double attributeValue);
    HashMap<String, Double> getAttributesMap();

    default boolean isSame(final UUID uuid) { return getUUID().equals(uuid); }

    void teleport(final CPPlayer<P> other);
    void teleport(final SimplifiedLocation location);

    @Override
    String getName();
    UUID getUUID();


    @Override
    void sendMessage(String message);
    void sendActionbar(String message);
}
