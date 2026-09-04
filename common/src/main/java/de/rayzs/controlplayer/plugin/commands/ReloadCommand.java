package de.rayzs.controlplayer.plugin.commands;

import de.rayzs.controlplayer.api.ControlPlayerAPI;
import de.rayzs.controlplayer.api.player.CPSender;
import de.rayzs.controlplayer.plugin.config.ConfigData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReloadCommand extends Command {

    private final ControlPlayerAPI api;

    public ReloadCommand(final ControlPlayerAPI api, final List<String> commands) {
        super(
                commands.get(0),
                "Reload all plugin related files and settings.",
                "",
                commands.size() > 1 ? commands : new ArrayList<>()
        );

        this.api = api;
        setPermission("controlplayer.reload");
    }

    @Override
    public boolean execute(final @NotNull CommandSender bukkitSender, final @NotNull String label, final @NotNull String[] args) {
        final CPSender sender = api.getPlayerConverter().convertSender(bukkitSender);
        ConfigData.Message.RELOAD_PROCESSING.send(sender);

        api.reload();

        ConfigData.Message.RELOAD_DONE.send(sender);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
