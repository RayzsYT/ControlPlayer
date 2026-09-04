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

public class ForceControlCommand extends Command {

    private final ControlPlayerAPI api;

    public ForceControlCommand(final ControlPlayerAPI api, final List<String> commands) {
        super(
                commands.get(0),
                "Force someone to be controlled by another player.",
                " <controller> <victim>",
                commands.size() > 1 ? commands : new ArrayList<>()
        );

        this.api = api;
        setPermission("controlplayer.forcecontrol");
    }

    @Override
    public boolean execute(final @NotNull CommandSender bukkitSender, final @NotNull String label, final @NotNull String[] args) {
        final CPSender sender = api.getPlayerConverter().convertSender(bukkitSender);

        if (args.length > 1) {
            final Player bukkitController = Bukkit.getPlayer(args[0]);
            if (bukkitController == null) {
                ConfigData.Message.NOT_ONLINE.send(sender, args[0]);
                return true;
            }

            final CPPlayer controller = api.getPlayerConverter().convertPlayer(bukkitController);
            final Session controllerSession = api.getSessionProvider().getSession(controller);

            if (controllerSession != null) {

                if (bukkitController.isDead()) {
                    ConfigData.Message.NOT_ALIVE.send(sender, "%player%", bukkitController.getName());
                    return true;
                }

                if (controllerSession.getSessionHolder().isSame(controller)) {
                    ConfigData.Message.OTHER_ALREADY_CONTROLLING.send(sender, "%player%", controller.getName());
                } else {
                    ConfigData.Message.OTHER_ALREADY_CONTROLLED.send(sender, "%player%", controller.getName());
                }

                return true;
            }

            final Player bukkitVictim = Bukkit.getPlayer(args[1]);
            if (bukkitVictim == null) {
                ConfigData.Message.NOT_ONLINE.send(sender, args[1]);
                return true;
            }

            final CPPlayer victim = api.getPlayerConverter().convertPlayer(bukkitVictim);
            final Session victimSession = api.getSessionProvider().getSession(victim);

            if (victimSession != null) {

                if (bukkitVictim.isDead()) {
                    ConfigData.Message.NOT_ALIVE.send(sender, "%player%", bukkitVictim.getName());
                    return true;
                }

                if (victimSession.getSessionHolder().isSame(victim)) {
                    ConfigData.Message.OTHER_ALREADY_CONTROLLING.send(sender, "%player%", victim.getName());
                } else {
                    ConfigData.Message.OTHER_ALREADY_CONTROLLED.send(sender, "%player%", victim.getName());
                }

                return true;
            }


            if (controller.isSame(victim)) {
                ConfigData.Message.OTHER_SAME.send(sender);
                return true;
            }


            final Session newSession = api.getSessionProvider().createAndReturnSession(controller, victim);

            if (newSession != null) {
                ConfigData.Message.OTHER_SUCCESS.send(sender, "%player%", controller.getName(), "%victim%", victim.getName());
            } else {
                ConfigData.Message.ERROR.send(sender);
            }

            return true;
        }

        ConfigData.Message.OTHER_USAGE.send(sender, "%label%", label);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String[] args) throws IllegalArgumentException {
        if (args.length > 2) return Collections.emptyList();


        final UUID controllerUUID = sender instanceof Player player ? player.getUniqueId() : null;
        final List<String> results = new ArrayList<>();


        if (args.length < 2) {
            results.addAll(Bukkit.getOnlinePlayers().stream().filter(other ->
                    api.getSessionProvider().canControl(
                            controllerUUID,
                            other.getUniqueId()
                    )).map(Player::getName).toList());
        } else {
            results.addAll(Bukkit.getOnlinePlayers().stream().filter(other ->
                    !args[0].equalsIgnoreCase(other.getName()) && api.getSessionProvider().canControl(
                            controllerUUID,
                            other.getUniqueId()
                    )).map(Player::getName).toList());
        }


        return results.stream().filter(suggestion ->
                suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())
        ).toList();
    }
}
