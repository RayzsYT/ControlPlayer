package de.rayzs.controlplayer.api;

public class ControlPlayer {

    private ControlPlayer() {}


    private static ControlPlayerAPI API;


    public static void set(final ControlPlayerAPI api) {
        if (API != null) {
            throw new IllegalStateException("API is already set");
        }

        API = api;
    }


    public static ControlPlayerAPI get() {
        if (API == null) {
            throw new IllegalStateException("API not initialized yet!");
        }

        return API;
    }
}
