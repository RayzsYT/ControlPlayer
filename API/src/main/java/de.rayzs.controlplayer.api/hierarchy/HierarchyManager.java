package de.rayzs.controlplayer.api.hierarchy;

import de.rayzs.controlplayer.api.adapter.LuckPermsAdapter;
import org.bukkit.entity.Player;
import java.util.*;

public class HierarchyManager {

    private static boolean initialized = false;

    public static void initialize() {
        initialized = true;
    }

    public static boolean isHigher(Player executor, Player target) {
        if(target.hasPermission("controlplayer.bypass") || target.hasPermission("*")) return false;
        if(!initialized) return true;

        List<Integer> executorList = new ArrayList<>(), targetList = new ArrayList<>();
        LuckPermsAdapter.getHierarchyPerms(executor).forEach(perms -> {
            try {
                executorList.add(Integer.parseInt(perms.replaceFirst("controlplayer.bypass.", "")));
            } catch (Throwable throwable) {
                System.err.println("ControlPlayer could not understand permission of " + executor.getName() + "! (" + perms + ")");
            }
        });

        LuckPermsAdapter.getHierarchyPerms(target).forEach(perms -> {
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
