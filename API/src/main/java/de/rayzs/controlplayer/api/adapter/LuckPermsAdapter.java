package de.rayzs.controlplayer.api.adapter;

import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.*;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import net.luckperms.api.*;
import java.util.*;

public class LuckPermsAdapter {

    private static LuckPerms provider;
    private static UserManager manager;

    public static void initialize() {
        provider = LuckPermsProvider.get();
        manager = provider.getUserManager();
    }

    public static List<String> getSpecificPerms(Player player) {
        List<String> result = new ArrayList<>();
        User user = manager.getUser(player.getName());
        if(user == null) return result;

        CachedDataManager data = user.getCachedData();
        result = data.getPermissionData().getPermissionMap().keySet().stream().filter(perms -> perms.startsWith("controlplayer.specific.")).collect(Collectors.toList());
        return result;
    }

    public static List<String> getHierarchyPerms(Player player) {
        List<String> result = new ArrayList<>();
        User user = manager.getUser(player.getName());
        if(user == null) return result;

        CachedDataManager data = user.getCachedData();
        result = data.getPermissionData().getPermissionMap().keySet().stream().filter(perms -> perms.startsWith("controlplayer.bypass.")).collect(Collectors.toList());
        return result;
    }
}
