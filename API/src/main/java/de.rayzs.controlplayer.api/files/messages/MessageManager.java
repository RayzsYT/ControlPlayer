package de.rayzs.controlplayer.api.files.messages;

import de.rayzs.controlplayer.api.configurator.FileConfigurator;

import java.util.HashMap;

public class MessageManager {

    private static FileConfigurator FILE = new FileConfigurator("messages", "./plugins/ControlPlayer");
    private final static HashMap<MessageType, String> MESSAGES = new HashMap<>();
    private final static String MESSAGE_PATH = "messages.";

    static { reload(true); }

    public static void reload(boolean firstLoad) {
        if(!firstLoad) {
            FILE = new FileConfigurator("messages", "./plugins/ControlPlayer");
            MESSAGES.clear();
        }
        MessageType[] messageTypes = MessageType.values();

        for (MessageType messageType : messageTypes) {
            String messagePath = MESSAGE_PATH + messageType.toString().toLowerCase();
            if(FILE.loadDefault()) setMessageToFile(messageType);
            Object obj = FILE.get(messagePath);
            if(obj == null) {
                System.out.println("Resetted '" + messagePath + "' from " + FILE.getFile().getName() + " because the value is null!");
                setMessageToFile(messageType);
                obj = FILE.get(messagePath);
            }
            String message = (String) obj;
            MESSAGES.put(messageType, message);
        }
    }

    public static String getMessage(MessageType messageType) {
        return MESSAGES.get(messageType).replace("%prefix%", MESSAGES.get(MessageType.PREFIX));
    }

    protected static void setMessageToFile(MessageType messageType) {
        String defaultPath = MESSAGE_PATH + messageType.toString().toLowerCase(), defaultMessage = "%prefix% ";
        switch (messageType) {
            case PREFIX:
                FILE.set(defaultPath, "&8[&4&lC&c&lP&8]");
                break;
            case CONTROLLING_ACTIONBAR_TEXT:
                FILE.set(defaultPath, "&aYou are controlling %player%");
                break;
            case WAITING_ACTIONBAR_TEXT:
                FILE.set(defaultPath, "&eLEFT CLICK 3x &7to toggle control-mode!");
                break;
            case CANNOT_SPECIFIC_CONTROL:
                FILE.set(defaultPath, defaultMessage + "&cYou can only control specific chosen players!");
                break;
            case BEING_CONTROLLED:
                FILE.set(defaultPath, defaultMessage + "You were being controlled right now!");
                break;
            case NORMAL_USAGE:
                FILE.set(defaultPath, defaultMessage + "&7Use &e/cp [player] &7to control someone. Execute this command again to stop controlling the player.");
                break;
            case SILENT_USAGE:
                FILE.set(defaultPath, defaultMessage + "&7Use &e/scp [player] &7to control someone. Execute this command again to stop controlling the player.");
                break;
            case NORMAL_SUCCESS:
                FILE.set(defaultPath, defaultMessage + "&aYou are controlling %player%!");
                break;
            case SILENT_SUCCESS:
                FILE.set(defaultPath, defaultMessage + "&aYou are controlling %player%! Press &eLEFT CLICK 3x &ato take control of the player. Hold &eSNEAK &aand click &eLEFT CLICK 3x §ato free the player again!");
                break;
            case ERROR:
                FILE.set(defaultPath, defaultMessage + "&cSomething went wrong!");
                break;
            case NOT_ALIVE:
                FILE.set(defaultPath, defaultMessage + "&cThe player you want to control isn't alive. (dead)");
                break;
            case ALREADY_CONTROLLED:
                FILE.set(defaultPath, defaultMessage + "&cSomeone else is already controlling this player!");
                break;
            case NO_PERMISSION:
                FILE.set(defaultPath, defaultMessage + "&cMissing permissions!");
                break;
            case NOT_ONLINE:
                FILE.set(defaultPath, defaultMessage + "&7This player is not online!");
                break;
            case ONLY_PLAYERS:
                FILE.set(defaultPath, defaultMessage + "&cYou are not a player!");
                break;
            case PLAYER_IMUN:
                FILE.set(defaultPath, defaultMessage + "&cYou are not allowed to control this player!");
                break;
            case PLAYER_LEFT:
                FILE.set(defaultPath, defaultMessage + "&cThe player you have controlled left the server!");
                break;
            case SELF_CONTROL:
                FILE.set(defaultPath, defaultMessage + "&cYou can not control yourself!");
                break;
            case STOPPED_CONTROLLING:
                FILE.set(defaultPath, defaultMessage + "&aYou stopped controlling the player!");
                break;
            case PLAYER_DIED:
                FILE.set(defaultPath, defaultMessage + "&cThe player you controlled died!");
            case SPY_CHAT_MESSAGE:
                FILE.set(defaultPath, defaultMessage + "&2%player% &atried to use the chat while being controlled: &e%message%");
        }
        FILE.save();
    }
}
