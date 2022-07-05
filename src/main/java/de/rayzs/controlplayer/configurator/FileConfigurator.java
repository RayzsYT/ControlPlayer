package de.rayzs.controlplayer.configurator;

import java.io.File;

import de.rayzs.controlplayer.api.files.message.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileConfigurator {

    private String fileName, filePath;
    private File file;
    private YamlConfiguration configuration;
    private boolean loadDefault;

    public FileConfigurator(String fileName, Plugin plugin) {
        init(fileName, "./plugins/" + plugin.getDescription().getName());
    }

    public FileConfigurator(String fileName, String filePath) {
        init(fileName, filePath);
    }

    protected void init(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.file = new File(filePath + "/" + fileName + ".yml");
        this.loadDefault = !this.file.exists();
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public File getFile() {
        return file;
    }

    public void set(String path, MessageType messageType, Object obj) {
        this.configuration.set(path + "." + messageType.toString().toLowerCase(), obj);
    }

    public void set(String path, Object obj) {
        this.configuration.set(path, obj);
    }

    public Object get(String path, MessageType messageType) {
        Object obj = this.configuration.get(path + "." + messageType.toString().toLowerCase());
        if (obj instanceof String) {
            String objString = (String)obj;
            return ChatColor.translateAlternateColorCodes('&', objString);
        }
        return obj;
    }

    public Object get(String path) {
        Object obj = this.configuration.get(path);
        if (obj instanceof String) {
            String objString = (String)obj;
            return ChatColor.translateAlternateColorCodes('&', objString);
        }
        return obj;
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean loadDefault() {
        return this.loadDefault;
    }
}