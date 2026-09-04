package de.rayzs.controlplayer.plugin.hook.hooks;

import de.rayzs.controlplayer.api.ControlPlayer;
import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.api.utils.StringUtils;
import de.rayzs.controlplayer.plugin.hook.Hook;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlaceholderAPIHook implements Hook {

    private ControlPlayerAPI api;

    @Override
    public void start() {
        api = ControlPlayer.get();
        new Expansion().register();
    }

    public String replacePlaceholders(final CPPlayer player, @NotNull String text) {

        if (text.indexOf('%') != -1) {
            if (player != null && player.get() instanceof Player bukkitPlayer) {
                return PlaceholderAPI.setPlaceholders(bukkitPlayer, text);
            } else {
                return PlaceholderAPI.setPlaceholders(null, text);
            }
        }

        return text;
    }

    private class Expansion extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "controlplayer";
        }

        @Override
        public @NotNull String getAuthor() {
            return "Rayzs_YT";
        }

        @Override
        public @NotNull String getVersion() {
            return "3.0.0";
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
            final String[] split = params.split("_");

            if (player != null) {
                final Session session = api.getSessionProvider().getSession(player.getUniqueId());

                if (params.equalsIgnoreCase("in_session")) {
                    return session != null ? "true" : "false";
                }

                if (params.equalsIgnoreCase("is_session_holder")) {
                    return session != null && session.getSessionHolder().getUUID().equals(player.getUniqueId()) ? "true" : "false";
                }

                if (params.equalsIgnoreCase("is_session_victim")) {
                    return session != null && session.getSessionTarget().getUUID().equals(player.getUniqueId()) ? "true" : "false";
                }

                if (params.equalsIgnoreCase("is_controlling")) {
                    return session != null && session.getControllingPlayer().getUUID().equals(player.getUniqueId()) ? "true" : "false";
                }

                if (params.equalsIgnoreCase("is_being_controlled")) {
                    return session != null && session.getControlledPlayer().getUUID().equals(player.getUniqueId()) ? "true" : "false";
                }


                final int isControllingIndex = StringUtils.searchIndex("is_controlling_", params);
                final int isControlledByIndex = StringUtils.searchIndex("is_being_controlled_by_", params);

                if (isControllingIndex != -1) {
                    final String targetName = params.substring(isControllingIndex);
                    final Player targetPlayer = Bukkit.getPlayer(targetName);

                    if (session == null || targetPlayer == null) return "false";

                    return session.getControlledPlayer().getUUID().equals(targetPlayer.getUniqueId()) ? "true" : "false";
                }

                if (isControlledByIndex != -1) {
                    final String targetName = params.substring(isControlledByIndex);
                    final Player targetPlayer = Bukkit.getPlayer(targetName);

                    if (session == null || targetPlayer == null) return "false";

                    return session.getControllingPlayer().getUUID().equals(targetPlayer.getUniqueId()) ? "true" : "false";
                }


                final int isInSessionWithIndex = StringUtils.searchIndex("in_session_with_", params);

                if (isInSessionWithIndex != -1) {
                    final String targetName = params.substring(isInSessionWithIndex);
                    final Player targetPlayer = Bukkit.getPlayer(targetName);

                    if (session == null || targetPlayer == null) return "false";

                    final UUID controllerUUID = session.getControllingPlayer().getUUID();
                    final UUID controlledUUID = session.getControlledPlayer().getUUID();

                    return controllerUUID.equals(targetPlayer.getUniqueId()) || controlledUUID.equals(targetPlayer.getUniqueId())
                            ? "true" : "false";
                }
            }

            return super.onPlaceholderRequest(player, params);
        }
    }
}