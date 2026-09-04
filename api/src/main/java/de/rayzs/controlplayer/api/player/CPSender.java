package de.rayzs.controlplayer.api.player;

public interface CPSender {
    boolean isConsole();

    default String getName() {
        return "CONSOLE";
    }

    void sendMessage(String message);
}
