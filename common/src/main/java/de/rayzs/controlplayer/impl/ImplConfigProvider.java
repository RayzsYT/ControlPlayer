package de.rayzs.controlplayer.impl;

import de.rayzs.controlplayer.api.config.Config;
import de.rayzs.controlplayer.api.config.ConfigProvider;

import java.io.File;
import java.util.*;

public class ImplConfigProvider implements ConfigProvider {

    private final Map<String, Config> configs = new HashMap<>();


    // Create default files if the folder does not exist yet.
    public ImplConfigProvider() {
        final File folder = new File(DEFAULT_PLUGIN_FOLDER_PATH);

        if (folder.isDirectory()) {
            return;
        }

        if (!folder.mkdirs()) {
            throw new RuntimeException("Failed to create ControlPlayer folder! (" + DEFAULT_PLUGIN_FOLDER_PATH + ")");
        }

        // TO-DO:
        // Create implementation to load all files from inside project
        // into the plugins/ControlPlayer folder.
    }

    @Override
    public Config getOrCreate(final String fileName) {
        return getOrCreate(null, fileName);
    }

    @Override
    public Config getOrCreate(final String filePath, final String fileName) {
        final String path = DEFAULT_PLUGIN_FOLDER_PATH + (filePath != null ? ("/" + filePath) : "");
        final String id = path + "/" + fileName;

        Config config = configs.get(id);
        if (config != null) {
            return config;
        }

        config = new ImplConfig(path, fileName);

        configs.put(id, config);
        return config;
    }
}