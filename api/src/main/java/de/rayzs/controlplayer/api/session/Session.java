package de.rayzs.controlplayer.api.session;

import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.utils.Counter;

import java.util.HashMap;

public class Session<P> {

    private final CPPlayer<P> holder, target;
    private final Counter swapCounter;

    private long lastSwap = System.currentTimeMillis();

    private boolean isSemiSession = false,
                    swappedControl = false,
                    stopped = false;

    // For caching some data relevant to a single session only.
    private final HashMap<String, Object> data = new HashMap<>();

    public Session(
            final CPPlayer<P> holder,
            final CPPlayer<P> target,
            final boolean isSemiSession
    ) {
        this.holder = holder;
        this.target = target;
        this.isSemiSession = isSemiSession;
        this.swapCounter = new Counter(500);
    }

    /**
     * Stop session so it's delayed for deletion in the next iteration.
     */
    public void stopSession() {
        stopped = true;
    }

    /**
     * Is session stopped and thus delayed for deletion.
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Increases the count and swaps it until the criteria is fulfilled.
     * This is essentially used for the Semi-Control functionality.
     */
    public void countAndSwap() {
        if (stopped || !isSemiSession || System.currentTimeMillis() - lastSwap < 1500) return;
        swapCounter.add();

        if (swapCounter.getCount() >= 3) {
            swapCounter.set(0);
            swapControl();
        }
    }

    /**
     * Swap control direction. This essentially changes the direction whether the holder or victim controls the other.
     */
    public Session<P> swapControl() {
        lastSwap = System.currentTimeMillis();
        swappedControl = !swappedControl;
        return this;
    }

    /**
     * Write data.
     */
    public void cache(final String key, final Object obj) {
        data.put(key, obj);
    }

    /**
     * Get cached data.
     */
    public <T> T getCached(final String key, final Class<T> clazz) {
        final Object val = data.get(key);
        if (val == null) return null;

        return clazz.cast(val);
    }

    /**
     * Get cached data or default value if there's none.
     */
    public <T> T getCached(final String key, final Class<T> clazz, final T defaultValue) {
        final Object val = data.get(key);
        if (val == null) return defaultValue;

        return clazz.cast(val);
    }

    /**
     * Remove cached data.
     */
    public void removeCache(final String key) {
        data.remove(key);
    }

    /**
     * Is control swapped or not.
     */
    public boolean isControlSwapped() {
        return swappedControl;
    }

    /**
     * If the session is a semi control session or not.
     */
    public boolean isSemiSession() {
        return isSemiSession;
    }

    /**
     * Player currently controlling.
     */
    public CPPlayer<P> getControllingPlayer() {
        return swappedControl ? target : holder;
    }

    /**
     * Player currently being controlled.
     */
    public CPPlayer<P> getControlledPlayer() {
        return swappedControl ? holder : target;
    }

    /**
     * Session holder player.
     */
    public CPPlayer<P> getSessionHolder() {
        return holder;
    }

    /**
     * Target player of session.
     */
    public CPPlayer<P> getSessionTarget() {
        return target;
    }
}
