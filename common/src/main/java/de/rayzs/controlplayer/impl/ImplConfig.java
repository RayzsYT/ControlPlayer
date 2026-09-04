package de.rayzs.controlplayer.impl;

import de.rayzs.controlplayer.api.config.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Collection;
import java.io.File;

public class ImplConfig implements Config {

    private final String filePath, fileName;

    private File file;
    private YamlConfiguration configuration;

    public ImplConfig(final String filePath, final String fileName) {
        this.fileName = fileName;
        this.filePath = filePath;

        load();
    }

    private void load() {
        this.file = new File(filePath, fileName + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Config set(final String path, final String target, final Object object) {
        this.configuration.set(
                ((path != null) ? (path + ".") : "") + target,
                object instanceof String ? ((String) object).replace("§", "&") : object
        );

        return this;
    }

    @Override
    public ImplConfig set(final String target, final Object object) {
        set(null, target, object);
        return this;
    }

    @Override
    public ImplConfig setAndSave(final String path, final String target, final Object object) {
        set(path, target, object);
        save();

        return this;
    }

    @Override
    public ImplConfig setAndSave(final String target, final Object object) {
        set(target, object);
        save();

        return this;
    }

    @Override
    public <T> T getOrSet(final String path, final String target, final T t) {
        final Class<T> type = (Class<T>) t.getClass();
        final Object result = get(path, target, type);

        if (result != null) {
            return type.cast(result);
        }

        set(path, target, t);
        save();

        return type.cast(get(path, target, type));
    }

    @Override
    public <T> T getOrSet(final String target, final T t) {
        final Class<T> type = (Class<T>) t.getClass();
        final Object result = get(target, type);

        if (result != null) {
            return type.cast(result);
        }

        set(target, t);
        save();

        return type.cast(get(target, type));
    }

    @Override
    public <T> T get(final String target, final Class<T> type) {
        return get(null, target, type);
    }

    @Override
    public <T> T get(final String path, final String target, final Class<T> type) {
        final Object object = this.configuration.get(((path != null) ? (path + ".") : "") + target);

        if (object instanceof String str) {
            type.cast(str.replace("&", "§"));
        }

        return type.cast(object);
    }

    @Override
    public Collection<String> getKeys(final boolean deep) {
        return this.configuration.getKeys(deep);
    }

    @Override
    public Collection<String> getKeys(final String section, final boolean deep) {
        return this.configuration.getConfigurationSection(section).getKeys(deep);
    }
}