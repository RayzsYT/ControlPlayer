package de.rayzs.controlplayer.api.hierarchy;

import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.*;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import net.luckperms.api.*;
import java.util.*;

public class LuckPermsAdapter {

    private final LuckPerms provider;
    private final UserManager manager;

    public LuckPermsAdapter() {
        provider = LuckPermsProvider.get();
        manager = provider.getUserManager();
    }

    List<String> getHierarchyPerms(Player player) {
        List<String> result = new ArrayList<>();
        User user = manager.getUser(player.getName());
        if(user == null) return result;

        CachedDataManager data = user.getCachedData();
        result = data.getPermissionData().getPermissionMap().keySet().stream().filter(perms -> perms.startsWith("controlplayer.bypass.")).collect(Collectors.toList());
        return result;
    }
}
