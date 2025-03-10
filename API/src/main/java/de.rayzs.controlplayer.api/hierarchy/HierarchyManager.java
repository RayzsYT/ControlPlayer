package de.rayzs.controlplayer.api.hierarchy;

import org.bukkit.entity.Player;
import java.util.*;

public class HierarchyManager {

    private static LuckPermsAdapter luckPermsAdapter = null;
    private static boolean initialized = false;

    public static void initialize() {
        luckPermsAdapter = new LuckPermsAdapter();
        initialized = true;
    }

    public static boolean isHigher(Player executor, Player target) {
        if(target.hasPermission("controlplayer.bypass") || target.hasPermission("*")) return false;
        if(!initialized || luckPermsAdapter == null) return true;

        List<Integer> executorList = new ArrayList<>(), targetList = new ArrayList<>();
        luckPermsAdapter.getHierarchyPerms(executor).forEach(perms -> {
            try {
                executorList.add(Integer.parseInt(perms.replaceFirst("controlplayer.bypass.", "")));
            } catch (Throwable throwable) {
                System.err.println("ControlPlayer could not understand permission of " + executor.getName() + "! (" + perms + ")");
            }
        });

        luckPermsAdapter.getHierarchyPerms(target).forEach(perms -> {
            try {
                targetList.add(Integer.parseInt(perms.replaceFirst("controlplayer.bypass.", "")));
            } catch (Throwable throwable) {
                System.err.println("ControlPlayer could not understand permission of " + target.getName() + "! (" + perms + ")");
            }
        });

        if(targetList.isEmpty()) return true;
        if(executorList.isEmpty())
            return executor.isOp() || executor.hasPermission("controlplayer.use");

        Collections.sort(executorList);
        Collections.sort(targetList);

        int executorPosition = executorList.get(0), targetPosition = targetList.get(0);
        return executorPosition < targetPosition;
    }
}
