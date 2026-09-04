package de.rayzs.controlplayer.plugin.hook.hooks;

import de.rayzs.controlplayer.plugin.hook.Hook;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsHook implements Hook {

    private LuckPerms provider;

    @Override
    public void start() {
        provider = LuckPermsProvider.get();
    }


}
