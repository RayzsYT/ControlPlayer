package de.rayzs.controlplayer.api.player;

public interface PlayerConverter<P> {

    CPPlayer<P> convertPlayer(P player);
    CPSender convertSender(Object sender);
}
