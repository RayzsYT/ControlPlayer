package de.rayzs.controlplayer.api.config;

import java.util.Collection;

public interface Config {

    /**
     * Reloads the config.
     */
    void reload();

    /**
     * Saves the config changes.
     */
    void save();

    /**
     * Sets a value.
     *
     * @param path Path to the section.
     * @param target Target key.
     * @param object Value to set.
     * @return The current Config instance.
     */
    Config set(final String path, final String target, final Object object);

    /**
     * Sets a value.
     *
     * @param target Target key.
     * @param object Value to set.
     * @return The current Config instance.
     */
    Config set(final String target, final Object object);

    /**
     * Sets a value and saves the config.
     *
     * @param path Path to the section.
     * @param target Target key.
     * @param object Value to set.
     * @return The current Config instance.
     */
    Config setAndSave(final String path, final String target, final Object object);

    /**
     * Sets a value and saves the config.
     *
     * @param target Target key.
     * @param object Value to set.
     * @return The current Config instance.
     */
    Config setAndSave(final String target, final Object object);

    /**
     * Gets a value if it exists.
     * Returns assigned default value and sets it in the config otherwise.
     *
     * @param path The path to the section.
     * @param target The target key.
     * @param value Default value.
     * @return The existing or default value.
     */
    <T> T getOrSet(final String path, final String target, final T value);

    /**
     * Gets a value if it exists.
     * Returns assigned default value and sets it in the config otherwise.
     *
     * @param target The target key.
     * @param value Default value.
     * @return The existing or default value.
     */
    <T> T getOrSet(final String target, final T value);

    /**
     * Gets a value.
     *
     * @param target Target key.
     * @param type Return object type.
     * @return The value.
     */
    <T> T get(final String target, final Class<T> type);

    /**
     * Gets a value.
     *
     * @param path Path to the section.
     * @param target Target key.
     * @param type Return object type.
     * @return The value.
     */
    <T> T get(final String path, final String target, final Class<T> type);

    /**
     * Gets all keys.
     *
     * <pre>
     *     {@code
     *     Collection<String> keys = getKeys(false);
     *     // Example: ["monster1", "monster2", "settings"]
     *
     *     Collection<String> keys = getKeys(true);
     *     // Example: ["monster1", "monster1.hp", "monster1.items", "monster1.items.mainhand", "monster2"...]
     *     }
     * </pre>
     *
     * @param deep Whether to get keys deeply.
     * @return Collection of keys.
     */
    Collection<String> getKeys(final boolean deep);

    /**
     * Gets all keys from a section.
     *
     * <pre>
     *     {@code
     *     Collection<String> keys = getKeys("monster1", false);
     *     // Example: ["hp", "items"]
     *
     *     Collection<String> keys = getKeys("monster1", true);
     *     // Example: ["hp", "items", "items.mainhand"]
     *     }
     * </pre>
     *
     * @param section The section to get keys from.
     * @param deep Whether to get keys deeply.
     * @return Collection of keys.
     */
    Collection<String> getKeys(final String section, final boolean deep);
}