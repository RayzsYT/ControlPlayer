package de.rayzs.controlplayer.impl;

import de.rayzs.controlplayer.api.events.DuringControlEvent;
import de.rayzs.controlplayer.api.events.SessionStartedEvent;
import de.rayzs.controlplayer.api.events.SessionStoppedEvent;
import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.session.SessionProvider;
import de.rayzs.controlplayer.api.utils.SimplifiedLocation;
import de.rayzs.controlplayer.api.utils.SimplifiedPotionEffect;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ImplSessionProvider implements SessionProvider<Player> {

    private final SchedulerProvider schedulerProvider;

    private final List<Session> sessions = new ArrayList<>();
    private final HashMap<UUID, Session> sessionsMap = new HashMap<>();

    public ImplSessionProvider(final SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;

        this.schedulerProvider.createScheduler(scheduler -> {
            sessions.removeIf(session -> {
                if (session.isStopped()) {
                    return true;
                }

                sync(session);
                return false;
            });
            sessions.forEach(this::sync);
        },1, 1);
    }


    private void initialSync(final Session session) {
        final CPPlayer<Player> sessionHolderPlayer = session.getSessionHolder();
        final CPPlayer<Player> sessionTargetPlayer = session.getSessionTarget();


        storeData(sessionHolderPlayer, sessionHolderPlayer);

        session.cache("holder-could-see-target", sessionHolderPlayer.get().canSee(sessionTargetPlayer.get()));

        sessionHolderPlayer.get().hidePlayer(sessionTargetPlayer.get());
        sessionTargetPlayer.get().hidePlayer(sessionHolderPlayer.get());

        sessionTargetPlayer.setCollision(false);
        sessionHolderPlayer.get().getInventory().setHeldItemSlot(
                sessionTargetPlayer.get().getInventory().getHeldItemSlot()
        );

        sessionTargetPlayer.getAttributesMap().forEach(sessionHolderPlayer::setAttribute);

        sessionHolderPlayer.teleport(sessionTargetPlayer);
        syncData(sessionHolderPlayer, sessionTargetPlayer);

        Bukkit.getPluginManager().callEvent(new SessionStartedEvent(session));
    }

    private void sync(final Session session) {
        final CPPlayer<Player> sessionHolderPlayer = session.getSessionHolder();
        final CPPlayer<Player> sessionTargetPlayer = session.getSessionTarget();
        final CPPlayer<Player> controllingPlayer = session.getControllingPlayer();
        final CPPlayer<Player> controlledPlayer = session.getControlledPlayer();

        if (ConfigData.Setting.SEND_ACTIONBAR.getValue(Boolean.class)) {

            if (session.isSemiSession()) {
                if (session.isControlSwapped()) {
                    ConfigData.Message.SILENT_WAITING_ACTIONBAR.actionbar(sessionHolderPlayer, "%player%", sessionTargetPlayer.getName());
                } else {
                    ConfigData.Message.SILENT_CONTROLLING_ACTIONBAR.actionbar(sessionHolderPlayer, "%player%", sessionTargetPlayer.getName());
                }
            } else {
                ConfigData.Message.CONTROLLING_ACTIONBAR.actionbar(sessionHolderPlayer, "%player%", sessionTargetPlayer.getName());
            }

        }

        syncData(controlledPlayer, controllingPlayer);
        Bukkit.getPluginManager().callEvent(new DuringControlEvent(session));
    }


    @Override
    public Session<Player> createAndReturnSession(final CPPlayer holder, final CPPlayer target) {
        final Session session = new Session<>(holder, target, false);


        sessions.add(session);

        sessionsMap.put(holder.getUUID(), session);
        sessionsMap.put(target.getUUID(), session);


        initialSync(session);


        return session;
    }

    @Override
    public Session createAndReturnSemiSession(final CPPlayer holder, final CPPlayer target) {
        final Session session = new Session<>(holder, target, true).swapControl();


        initialSync(session);


        sessions.add(session);

        sessionsMap.put(holder.getUUID(), session);
        sessionsMap.put(target.getUUID(), session);

        return session;
    }

    @Override
    public boolean destroySession(final Session session) {
        final CPPlayer<Player> sessionHolderPlayer = session.getSessionHolder();
        final CPPlayer<Player> sessionTargetPlayer = session.getSessionTarget();

        sessionsMap.remove(sessionHolderPlayer.getUUID());
        sessionsMap.remove(sessionTargetPlayer.getUUID());

        loadData(sessionHolderPlayer, sessionHolderPlayer.getUUID());


        final HashSet<UUID> playersWhoCannotSeeHolder = playersWhoCannotSee.get(sessionHolderPlayer.getUUID());
        for (final Player other : Bukkit.getOnlinePlayers()) {
            if (!playersWhoCannotSeeHolder.contains(other.getUniqueId())) {
                other.showPlayer(sessionHolderPlayer.get());
            }
        }

        if ((boolean) session.getCached("holder-could-see-target", Boolean.class, true)) {
            sessionHolderPlayer.get().showPlayer(sessionTargetPlayer.get());
        }


        clearData(sessionHolderPlayer.getUUID());
        sessionTargetPlayer.setCollision(true);

        Bukkit.getPluginManager().callEvent(new SessionStoppedEvent(session));

        return sessions.remove(session);
    }

    @Override
    public boolean canControl(final UUID controllerUUID, final UUID victimUUID) {
        final Session session = getSession(victimUUID);
        return session == null;
    }

    @Override
    public Session getSession(final CPPlayer player) {
        return getSession(player.getUUID());
    }

    @Override
    public Session getSession(final UUID uuid) {
        return sessionsMap.get(uuid);
    }

    @Override
    public List<Session> getAllRunningSessions() {
        return sessions;
    }


    // Stored data with some general methods.

    private final HashMap<UUID, Integer> lastFoodLevel = new HashMap<>();
    private final HashMap<UUID, Integer> lastLevel = new HashMap<>();
    private final HashMap<UUID, Integer> lastTotalExperience = new HashMap<>();

    private final HashMap<UUID, Boolean> lastAllowedFlight = new HashMap<>();
    private final HashMap<UUID, Boolean> lastFlying = new HashMap<>();
    private final HashMap<UUID, Boolean> lastCollision = new HashMap<>();

    private final HashMap<UUID, ItemStack[]> lastInventory = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> lastArmor = new HashMap<>();

    private final HashMap<UUID, Double> lastHealth = new HashMap<>();
    private final HashMap<UUID, Double> lastHealthScale = new HashMap<>();

    private final HashMap<UUID, Float> lastExp = new HashMap<>();
    private final HashMap<UUID, Float> lastExhaustion = new HashMap<>();

    private final HashMap<UUID, SimplifiedLocation> lastLocation = new HashMap<>();
    private final HashMap<UUID, GameMode> lastGameMode = new HashMap<>();

    private final HashMap<UUID, HashSet<UUID>> playersWhoCannotSee = new HashMap<>();
    private final HashMap<UUID, HashSet<SimplifiedPotionEffect>> lastEffects = new HashMap<>();

    private final HashMap<UUID, HashMap<String, Double>> lastAttributes = new HashMap<>();


    private void storeData(final CPPlayer<Player> storeAtPlayer, final CPPlayer<Player> dataToStoreFromPlayer) {
        final Player bukkitStoreAtPlayer = storeAtPlayer.get();
        final Player bukkitDataToStoreFrom = dataToStoreFromPlayer.get();

        final UUID storeAt = bukkitStoreAtPlayer.getUniqueId();

        lastFoodLevel.put(storeAt, bukkitDataToStoreFrom.getFoodLevel());
        lastLevel.put(storeAt, bukkitDataToStoreFrom.getLevel());
        lastTotalExperience.put(storeAt, bukkitDataToStoreFrom.getTotalExperience());

        lastAllowedFlight.put(storeAt, bukkitDataToStoreFrom.getAllowFlight());
        lastFlying.put(storeAt, bukkitDataToStoreFrom.isFlying());
        lastCollision.put(storeAt, dataToStoreFromPlayer.canCollide());

        lastInventory.put(storeAt, bukkitDataToStoreFrom.getInventory().getContents().clone());
        lastArmor.put(storeAt, bukkitDataToStoreFrom.getInventory().getArmorContents().clone());

        lastHealth.put(storeAt, dataToStoreFromPlayer.getHealth());
        lastHealthScale.put(storeAt, dataToStoreFromPlayer.getHealthScale());

        lastExp.put(storeAt, bukkitDataToStoreFrom.getExp());
        lastExhaustion.put(storeAt, bukkitDataToStoreFrom.getExhaustion());

        final Location currentLocation = bukkitDataToStoreFrom.getLocation().clone();
        final SimplifiedLocation simplifiedLocation = new SimplifiedLocation(
                currentLocation.getWorld().getName(),
                currentLocation.getX(),
                currentLocation.getY(),
                currentLocation.getZ(),
                currentLocation.getYaw(),
                currentLocation.getPitch()
        );

        lastLocation.put(storeAt, simplifiedLocation);
        lastGameMode.put(storeAt, bukkitDataToStoreFrom.getGameMode());

        playersWhoCannotSee.put(storeAt, new HashSet<>(
                Bukkit.getOnlinePlayers().stream().filter(other ->
                        !other.canSee(bukkitStoreAtPlayer)
                ).map(Player::getUniqueId).toList())
        );

        lastEffects.put(storeAt, new HashSet<>(dataToStoreFromPlayer.getActivePotionEffects()));

        lastAttributes.put(storeAt, storeAtPlayer.getAttributesMap());
    }

    private void loadData(final CPPlayer<Player> player, final UUID loadDataFrom) {
        if (!player.isOnline()) return;


        final Player bukkitPlayer = player.get();

        bukkitPlayer.setFoodLevel(lastFoodLevel.get(loadDataFrom));
        bukkitPlayer.setLevel(lastLevel.get(loadDataFrom));
        bukkitPlayer.setTotalExperience(lastTotalExperience.get(loadDataFrom));

        bukkitPlayer.setAllowFlight(lastAllowedFlight.get(loadDataFrom));
        bukkitPlayer.setFlying(lastFlying.get(loadDataFrom));
        player.setCollision(lastCollision.get(loadDataFrom));

        bukkitPlayer.getInventory().setContents(lastInventory.get(loadDataFrom));
        bukkitPlayer.getInventory().setArmorContents(lastArmor.get(loadDataFrom));

        player.setHealthScale(lastHealthScale.get(loadDataFrom));
        player.setHealth(lastHealth.get(loadDataFrom));

        bukkitPlayer.setExp(lastExp.get(loadDataFrom));
        bukkitPlayer.setExhaustion(lastExhaustion.get(loadDataFrom));

        player.teleport(lastLocation.get(loadDataFrom));
        bukkitPlayer.setGameMode(lastGameMode.get(loadDataFrom));

        lastAttributes.get(loadDataFrom).forEach(player::setAttribute);

        bukkitPlayer.getActivePotionEffects().forEach(effect ->
                bukkitPlayer.removePotionEffect(effect.getType())
        );

        lastEffects.get(loadDataFrom).forEach(player::addPotionEffect);
    }

    private void syncData(final CPPlayer<Player> player, final CPPlayer<Player> source) {
        if (!player.isOnline()) return;


        final Player bukkitPlayer = player.get();
        final Player bukkitSourcePlayer = source.get();


        //player.teleport(source);
        bukkitPlayer.setExhaustion(bukkitSourcePlayer.getExhaustion());


        if (ConfigData.Setting.SYNC_FOOD_LEVEL.getValue(Boolean.class)) {
            bukkitPlayer.setFoodLevel(bukkitSourcePlayer.getFoodLevel());
        }

        if (ConfigData.Setting.SYNC_LEVEL.getValue(Boolean.class)) {
            bukkitPlayer.setLevel(bukkitSourcePlayer.getLevel());

            bukkitPlayer.setTotalExperience(bukkitSourcePlayer.getTotalExperience());
            bukkitPlayer.setExp(bukkitSourcePlayer.getExp());
        }

        if (ConfigData.Setting.SYNC_FLIGHT.getValue(Boolean.class)) {
            bukkitPlayer.setAllowFlight(bukkitSourcePlayer.getAllowFlight());
            bukkitPlayer.setFlying(bukkitSourcePlayer.isFlying());
        }

        if (ConfigData.Setting.SYNC_INVENTORY.getValue(Boolean.class)) {
            bukkitPlayer.getInventory().setContents(bukkitSourcePlayer.getInventory().getContents().clone());
            bukkitPlayer.getInventory().setArmorContents(bukkitSourcePlayer.getInventory().getArmorContents().clone());
        }

        if (ConfigData.Setting.SYNC_HEALTH.getValue(Boolean.class)) {
            player.setHealthScale(source.getHealthScale());
            player.setHealth(source.getHealth());
        }

        if (ConfigData.Setting.SYNC_GAMEMODE.getValue(Boolean.class)) {
            bukkitPlayer.setGameMode(bukkitSourcePlayer.getGameMode());
        }

        if (ConfigData.Setting.SYNC_EFFECT.getValue(Boolean.class)) {
            bukkitPlayer.getActivePotionEffects().forEach(effect ->
                    bukkitPlayer.removePotionEffect(effect.getType())
            );

            bukkitSourcePlayer.getActivePotionEffects().forEach(bukkitPlayer::addPotionEffect);
        }
    }

    private void clearData(final UUID playerUUID) {
        lastFoodLevel.remove(playerUUID);
        lastLevel.remove(playerUUID);
        lastTotalExperience.remove(playerUUID);

        lastAllowedFlight.remove(playerUUID);
        lastFlying.remove(playerUUID);
        lastCollision.remove(playerUUID);

        lastInventory.remove(playerUUID);
        lastArmor.remove(playerUUID);

        lastHealth.remove(playerUUID);
        lastHealthScale.remove(playerUUID);

        lastExp.remove(playerUUID);
        lastExhaustion.remove(playerUUID);

        lastLocation.remove(playerUUID);
        lastGameMode.remove(playerUUID);

        playersWhoCannotSee.remove(playerUUID);
        lastEffects.remove(playerUUID);
    }
}
