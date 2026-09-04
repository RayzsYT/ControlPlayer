package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.player.CPPlayer;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.api.session.Session;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ControlCommand extends Command {

    private final ControlPlayerAPI api;

    public ControlCommand(final ControlPlayerAPI api, final List<String> commands) {
        super(
                commands.get(0),
                "Control another player.",
                " <victim>",
                commands.size() > 1 ? commands : new ArrayList<>()
        );

        this.api = api;
        setPermission("controlplayer.control");
    }

    @Override
    public boolean execute(final @NotNull CommandSender bukkitSender, final @NotNull String label, final @NotNull String[] args) {
        final CPSender sender = api.getPlayerConverter().convertSender(bukkitSender);

        if (! (bukkitSender instanceof Player bukkitExecutor)) {
            ConfigData.Message.ONLY_PLAYERS.send(sender);
            return true;
        }

        final CPPlayer player = api.getPlayerConverter().convertPlayer(bukkitExecutor);
        final Session currentPlayerSession = api.getSessionProvider().getSession(player);

        if (currentPlayerSession != null) {

            if (currentPlayerSession.getSessionHolder().isSame(player)) {
                api.getSessionProvider().destroySession(currentPlayerSession);

                ConfigData.Message.NORMAL_STOPPED.send(player,
                        "%player%", currentPlayerSession.getSessionTarget().getName()
                );
            } else {
                ConfigData.Message.BEING_CONTROLLED.send(player);
            }

            return true;
        }

        if (args.length > 0) {
            final Player bukkitTarget = Bukkit.getPlayer(args[0]);
            if (bukkitTarget == null) {
                ConfigData.Message.NOT_ONLINE.send(player, "%player%", args[0]);
                return true;
            }

            if (player.isSame(bukkitTarget.getUniqueId())) {
                ConfigData.Message.SELF_CONTROL.send(player);
                return true;
            }

            if (bukkitTarget.isDead()) {
                ConfigData.Message.NOT_ALIVE.send(player, "%player%", bukkitTarget.getName());
                return true;
            }

            final CPPlayer target = api.getPlayerConverter().convertPlayer(bukkitTarget);
            final Session targetSession = api.getSessionProvider().getSession(target);

            if (targetSession != null) {

                if (targetSession.getSessionHolder().isSame(target)) {
                    ConfigData.Message.NORMAL_ALREADY_CONTROLLING.send(player, "%player%", target.getName());
                } else {
                    ConfigData.Message.NORMAL_ALREADY_CONTROLLED.send(player, "%player%", target.getName());
                }

                return true;
            }

            final Session newSession = api.getSessionProvider().createAndReturnSession(player, target);

            if (newSession != null) {
                ConfigData.Message.NORMAL_SUCCESS.send(player, "%player%", target.getName());
            } else {
                ConfigData.Message.ERROR.send(player);
            }

            return true;
        }


        ConfigData.Message.NORMAL_USAGE.send(player, "%label%", label);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String[] args) throws IllegalArgumentException {
        if (args.length > 1) return Collections.emptyList();


        final UUID controllerUUID = sender instanceof Player player ? player.getUniqueId() : null;
        final List<String> results = Bukkit.getOnlinePlayers().stream().filter(other ->
                !other.getUniqueId().equals(controllerUUID) && api.getSessionProvider().canControl(
                        controllerUUID,
                        other.getUniqueId()
                )).map(Player::getName).toList();

        return results.stream().filter(suggestion ->
                suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())
        ).toList();
    }
}
