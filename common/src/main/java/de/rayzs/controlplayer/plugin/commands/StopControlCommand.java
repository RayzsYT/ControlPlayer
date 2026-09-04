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

public class StopControlCommand extends Command {

    private final ControlPlayerAPI api;

    public StopControlCommand(final ControlPlayerAPI api, final List<String> commands) {
        super(
                commands.get(0),
                "Force someone to stop controlling.",
                " <controller>",
                commands.size() > 1 ? commands : new ArrayList<>()
        );

        this.api = api;
        setPermission("controlplayer.stop");
    }

    @Override
    public boolean execute(final @NotNull CommandSender bukkitSender, final @NotNull String label, final @NotNull String[] args) {
        final CPSender sender = api.getPlayerConverter().convertSender(bukkitSender);
        if (args.length == 0) {
            ConfigData.Message.STOP_USAGE.send(sender, "%label%", label);
            return true;
        }


        final Player bukkitTarget = Bukkit.getPlayer(args[0]);

        if (bukkitTarget == null) {
            ConfigData.Message.NOT_ONLINE.send(sender, "%player%", args[0]);
            return true;
        }

        final CPPlayer target = api.getPlayerConverter().convertPlayer(bukkitTarget);
        final Session session = api.getSessionProvider().getSession(target);

        if(session == null || !session.getSessionHolder().isSame(target)) {
            ConfigData.Message.STOP_NO_SESSION.send(sender, "%player%", target.getName());
            return true;
        }

        api.getSessionProvider().destroySession(session);
        ConfigData.Message.STOP_SUCCESS.send(sender, "%player%", target.getName());
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String[] args) throws IllegalArgumentException {
        if (args.length > 1) return Collections.emptyList();


        final List<String> results = Bukkit.getOnlinePlayers().stream().filter(other -> {
            final Session session = api.getSessionProvider().getSession(other.getUniqueId());
            if (session == null) return false;

            return session.getSessionHolder().isSame(other.getUniqueId());
        }).map(Player::getName).toList();

        return results.stream().filter(suggestion ->
                suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())
        ).toList();
    }
}
