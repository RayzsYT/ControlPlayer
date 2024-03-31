package de.rayzs.controlplayer.api.files.settings;

import de.rayzs.controlplayer.api.configurator.FileConfigurator;

import java.util.HashMap;

public class SettingsManager {

    private static FileConfigurator FILE = new FileConfigurator("config", "./plugins/ControlPlayer");
    private final static HashMap<SettingType, Object> SETTINGS = new HashMap<>();
    private final static String SETTING_PATH = "settings.";

    static { reload(true); }

    public static void reload(boolean firstLoad) {
        if(!firstLoad) {
            FILE = new FileConfigurator("config", "./plugins/ControlPlayer");
            SETTINGS.clear();
        }
        SettingType[] settingTypes = SettingType.values();

        for (SettingType settingType : settingTypes) {
            String settingPath = SETTING_PATH + settingType.toString().toLowerCase().replace("_", ".");
            if(FILE.loadDefault()) setSettingToFile(settingType);
            Object obj = FILE.get(settingPath);
            if(obj == null) {
                System.out.println("Resetted '" + settingPath + "' from " + FILE.getFile().getName() + " because the value is null!");
                setSettingToFile(settingType);
                obj = FILE.get(settingPath);
            }
            SETTINGS.put(settingType, obj);
        }
    }

    public static Object getSetting(SettingType settingType) {
        return SETTINGS.get(settingType);
    }

    protected static void setSettingToFile(SettingType settingType) {
        String defaultPath = SETTING_PATH + settingType.toString().toLowerCase().replace("_", ".");

        switch (settingType) {
            case APIMODE:
            case SYSTEM_ASYNCCHAT:
            case SYSTEM_IGNOREBYPASS:
            case CONTROL_RUNNING_CANCELCHAT: case CONTROL_RUNNING_CANCELCOMMANDS:
                FILE.set(defaultPath, false); break;
            case UPDATER_DELAY: FILE.set(defaultPath, 18000); break;
            case CONTROL_RUNNING_SYNCDELAY: FILE.set(defaultPath, 0); break;
            case CONTROL_RUNNING_FORCECHAT_BYPASSMESSAGE: FILE.set(defaultPath, "-b "); break;
            default: FILE.set(defaultPath, true); break;
        }
        FILE.save();
    }
}
