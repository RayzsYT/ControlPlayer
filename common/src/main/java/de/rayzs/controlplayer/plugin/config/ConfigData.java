package de.rayzs.controlplayer.plugin.config;

import de.rayzs.controlplayer.api.ControlPlayer;
import de.rayzs.controlplayer.api.config.Config;
import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.plugin.hook.PluginHooks;
import de.rayzs.controlplayer.plugin.hook.hooks.PlaceholderAPIHook;
import java.util.Arrays;

public class ConfigData {


    private static final Config CONFIG_SETTINGS = ControlPlayer.get().getConfigProvider().getOrCreate("config");
    private static final Config CONFIG_MESSAGE = ControlPlayer.get().getConfigProvider().getOrCreate("messages");


    private ConfigData() {}


    public static void reload() {
        CONFIG_SETTINGS.reload();
        CONFIG_MESSAGE.reload();

        for (final Setting setting : Setting.values()) {
            setting.load();
        }

        for (final Message message : Message.values()) {
            message.load();
        }
    }


    public enum Message {

        PREFIX(
                "prefix",
                "&8[&4&lC&c&lP&8]"
        ),

        ERROR(
                "error",
                "%prefix% &cSomething went wrong!"
        ),

        UPDATE_OUTDATED_MESSAGE(
                "update_outdated_message",
                "%prefix% &cA new version of ControlPlayer is available!"
        ),

        UPDATE_UPDATED_MESSAGE(
                "update_updated_message",
                "%prefix% &aYou are using the latest version of ControlPlayer!"
        ),

        ONLY_PLAYERS(
                "only_players",
                "%prefix% &cYou are not a player!"
        ),

        NOT_ALIVE(
                "not_alive",
                "%prefix% &c%player% is not alive!"
        ),

        NOT_ONLINE(
                "not_online",
                "%prefix% &c%player% is not online!"
        ),

        SELF_CONTROL(
                "self_control",
                "%prefix% &cYou cannot control yourself!"
        ),

        PLAYER_IMMUNE(
                "player_immune",
                "%prefix% &cYou are not allowed to control %player%!"
        ),

        BEING_CONTROLLED(
                "being_controlled",
                "%prefix% &cYou are being controlled right now!"
        ),

        CANNOT_SPECIFIC_CONTROL(
                "cannot_specific_control",
                "%prefix% &cYou can only control specific chosen players!"
        ),

        PLAYER_DIED(
                "player_died",
                "%prefix% &cThe player you were controlling just died!"
        ),

        PLAYER_LEFT(
                "player_left",
                "%prefix% &cThe player you were controlling just left the server!"
        ),

        CONTROLLING_ACTIONBAR(
                "controlling_actionbar",
                "&aYou are controlling %player%"
        ),

        SPY_CHAT_NOTIFICATION(
                "spy_chat_notification",
                "%prefix% &2%player% &atried to use the chat while being controlled: &e%message%"
        ),

        SILENT_WAITING_ACTIONBAR(
                "silent_waiting_actionbar",
                "&eLEFT CLICK 3x &7to start controlling!"
        ),

        SILENT_CONTROLLING_ACTIONBAR(
                "silent_controlling_actionbar",
                "&eLEFT CLICK 3x + SNEAK &7to start observing again!"
        ),

        NORMAL_USAGE(
                "normal_usage",
                "%prefix% &7Use &e/%label% [player] &7to control someone. Execute this command again to stop controlling."
        ),

        NORMAL_SUCCESS(
                "normal_success",
                "%prefix% &aYou are controlling %player%!"
        ),

        NORMAL_STOPPED(
                "normal_stopped",
                "%prefix% &aYou stopped controlling %player%!"
        ),

        NORMAL_ALREADY_CONTROLLING(
                "normal_already_controlling",
                "%prefix% &c%prefix% is already controlling someone!"
        ),

        NORMAL_ALREADY_CONTROLLED(
                "normal_already_controlled",
                "%prefix% &c%player% is already being controlled by someone!"
        ),

        SEMI_USAGE(
                "semi_usage",
                "%prefix% &7Use &e/%label% [player] &7to semi-control someone. Execute this command again to stop controlling the player."
        ),

        SEMI_SUCCESS(
                "semi_success",
                "%prefix% &aYou are controlling %player%! Press &eLEFT CLICK 3x &ato start controlling the player. &eLEFT CLICK 3x + hold SNEAK &ato start observing again."
        ),

        SEMI_STOPPED(
                "semi_stopped",
                "%prefix% &aYou stopped controlling %player%!"
        ),

        SEMI_ALREADY_CONTROLLING(
                "semi_already_controlling",
                "%prefix% &c%player% is already controlling someone!"
        ),

        SEMI_ALREADY_CONTROLLED(
                "semi_already_controlled",
                "%prefix% &c%player% is already being controlled by someone!"
        ),

        OTHER_USAGE(
                "other_usage",
                "%prefix% &7Use &e/%label% [controller] [victim] &7to force someone control somebody. Execute this command again to stop the control."
        ),

        OTHER_SUCCESS(
                "other_success",
                "%prefix% &aYou forced %player% to control %victim%!"
        ),

        OTHER_STOPPED(
                "other_stopped",
                "%prefix% &a%player% is no longer controlling %victim%!"
        ),

        OTHER_SAME(
                "other_same",
                "%prefix% &cController and victim cannot be the same!"
        ),

        OTHER_ALREADY_CONTROLLING(
                "other_already_controlling",
                "%prefix% &c%player% is already controlling someone!"
        ),

        OTHER_ALREADY_CONTROLLED(
                "other_already_controlled",
                "%prefix% &c%player% is already being controlled by someone!"
        ),

        STOP_USAGE(
                "stop_usage",
                "%prefix% &7Use &e/%label% [controller] &7to force someone to stop controlling."
        ),

        STOP_NO_SESSION(
                "stop_no_session",
                "%prefix% &c%player% is not controlling anybody!"
        ),

        STOP_SUCCESS(
                "stop_success",
                "%prefix% &aYou forced %player% to stop controlling!"
        ),

        FIX_SELF_SUCCESS(
                "fix_self_success",
                "%prefix% &aFixed yourself!"
        ),

        FIX_SELF_IN_SESSION(
                "fix_self_in_session",
                "%prefix% &cIs currently being controlled or controlling someone."
        ),

        FIX_OTHER_SUCCESS(
                "fix_other_success",
                "%prefix% &aFixed %player%!"
        ),

        FIX_OTHER_IN_SESSION(
                "fix_other_in_session",
                "%prefix% &c%player% is currently being controlled or controlling someone!"
        ),

        RELOAD_PROCESSING(
        "reload_processing",
                "&eReloading all files..."
        ),

        RELOAD_DONE(
        "reload_done",
                "&aDone!"
        );

        private final String path, defaultMessage;
        private String message;

        Message(
                final String path,
                final String defaultMessage
        ) {
            this.path = path;
            this.defaultMessage = defaultMessage;
        }

        public String getPath() {
            return path;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }

        public String getLoadedMessage() {
            return message;
        }

        public void load() {
            message = CONFIG_MESSAGE.getOrSet(path, defaultMessage);
        }

        public void send(final CPSender sender, final String... replacements) {
            final String sendingMessage = PluginHooks.PLACEHOLDERAPI.modifyIfExist(
                    prepareMessage(replacements),
                    (PlaceholderAPIHook hook, String input) -> hook.replacePlaceholders(null, input)
            );

            sender.sendMessage(sendingMessage);
        }

        public void send(final CPPlayer player, final String... replacements) {
            final String sendingMessage = PluginHooks.PLACEHOLDERAPI.modifyIfExist(
                    prepareMessage(replacements),
                    (PlaceholderAPIHook hook, String input) -> hook.replacePlaceholders(player, input)
            );

            player.sendMessage(sendingMessage);
        }

        public void actionbar(final CPPlayer player, final String... replacements) {
            final String sendingMessage = PluginHooks.PLACEHOLDERAPI.modifyIfExist(
                    prepareMessage(replacements),
                    (PlaceholderAPIHook hook, String input) -> hook.replacePlaceholders(player, input)
            );

            player.sendActionbar(sendingMessage);
        }

        private String prepareMessage(final String... replacements) {
            String sendingMessage = message;

            for (int i = 0; i < replacements.length / 2; i++) {
                sendingMessage = sendingMessage.replace(replacements[i * 2], replacements[i * 2 + 1]);
            }

            return sendingMessage.replace("%prefix%", ConfigData.Message.PREFIX.getLoadedMessage());
        }
    }


    public enum Setting {


        UPDATER_ENABLED(
                "updater.enabled",
                true
        ),

        UPDATER_DELAY(
                "updater.delay",
                18000
        ),

        CONTROL_OP_PLAYERS(
                "control_op_players",
                false
        ),

        VICTIM_CAN_DROP_ITEMS(
                "settings.victim_can_drop_items",
                false
        ),

        VICTIM_CAN_USE_CHAT(
                "settings.victim_can_use_chat",
                true
        ),

        VICTIM_CAN_USE_COMMANDS(
                "settings.victim_can_use_commands",
                true
        ),

        SYNC_TELEPORTATION(
                "settings.sync_teleportation",
                true
        ),

        SPY_ON_VICTIM_CHAT(
                "settings.spy_on_victim_chat",
                true
        ),

        SEND_ACTIONBAR(
                "settings.send_actionbar",
                true
        ),

        ALWAYS_CHAT_AS_VICTIM(
                "chat.always_chat_as_victim",
                true
        ),

        BYPASS_PREFIX(
                "chat.bypass_prefix",
                "-b "
        ),

        SYNC_INVENTORY(
                "sync.inventory",
                true
        ),

        SYNC_LEVEL(
                "sync.level",
                true
        ),

        SYNC_HEALTH(
                "sync.health",
                true
        ),

        SYNC_FOOD_LEVEL(
                "sync.food_level",
                true
        ),

        SYNC_GAMEMODE(
                "sync.gamemode",
                true
        ),

        SYNC_FLIGHT(
                "sync.flight",
                true
        ),

        SYNC_EFFECT(
                "sync.effect",
                true
        ),

        SYNC_ATTRIBUTES(
                "sync.attributes",
                true
        ),

        COMMAND_ALIASES_CONTROL(
                "command-aliases.control",
                Arrays.asList("controlplayer", "control", "cp")
        ),

        COMMAND_ALIASES_SEMICONTROL(
                "command-aliases.semicontrol",
                Arrays.asList("semicontrol", "sc")
        ),

        COMMAND_ALIASES_FORCECONTROL(
                "command-aliases.forcecontrol",
                Arrays.asList("forcecontrol", "fc")
        ),

        COMMAND_ALIASES_STOPCONTROL(
                "command-aliases.stopcontrol",
                Arrays.asList("stopcontrol", "sc")
        ),

        COMMAND_ALIASES_FIX(
                "command-aliases.fix",
                Arrays.asList("controlplayerfix", "cpfix", "cpf")
        ),

        COMMAND_ALIASES_RELOAD(
                "command-aliases.reload",
                Arrays.asList("controlplayerreload", "cpr")
        );



        private final String path;
        private final Object defaultValue;
        private Object value;

        Setting(
                final String path,
                final Object defaultValue
        ) {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        public String getPath() {
            return path;
        }

        public <T> T getDefaultValue(final Class<T> type) {
            return type.cast(defaultValue);
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void load() {
            value = CONFIG_SETTINGS.getOrSet(path, defaultValue);
        }

        public <T> T getValue(final Class<T> type) {
            return type.cast(value);
        }

        public Object getValue() {
            return value;
        }
    }

}
