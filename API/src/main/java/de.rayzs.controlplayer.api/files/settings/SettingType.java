package de.rayzs.controlplayer.api.files.settings;

public enum SettingType {

    APIMODE,

    UPDATER_ENABLED, UPDATER_DELAY,

    COMMANDALIASES_CONTROL, COMMANDALIASES_FIX, COMMANDALIASES_RELOAD, COMMANDALIASES_SILENTCONTROL,

    SYSTEM_ASYNCCHAT, SYSTEM_IGNOREBYPASS,

    CONTROL_RUNNING_SYNC_EFFECT,

    CONTROL_RUNNING_FORCECHAT_ENABLED, CONTROL_RUNNING_FORCECHAT_BYPASSMESSAGE, CONTROL_RUNNING_SYNCDELAY, CONTROL_RUNNING_ACTIONBAR_ENABLED,
    CONTROL_RUNNING_CANCELCHAT, CONTROL_RUNNING_SPYCHAT,

    CONTROL_RUNNING_DROPITEMS, CONTROL_RUNNING_CANCELCOMMANDS, CONTROL_RUNNING_SYNCTELEPORT,

    CONTROL_STOP_RETURN_INVENTORY, CONTROL_STOP_RETURN_LOCATION,
    CONTROL_STOP_RETURN_LEVEL, CONTROL_STOP_RETURN_HEALTH,
    CONTROL_STOP_RETURN_FOODLEVEL, CONTROL_STOP_RETURN_GAMEMODE,
    CONTROL_STOP_RETURN_FLIGHT, CONTROL_STOP_RETURN_EFFECT,
}
