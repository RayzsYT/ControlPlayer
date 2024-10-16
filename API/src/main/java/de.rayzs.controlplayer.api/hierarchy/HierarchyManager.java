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
        if(!initialized || luckPermsAdapter == null) return true;

        List<Integer> executorList = new ArrayList<>(), targetList = new ArrayList<>();
        luckPermsAdapter.getHierarchyPerms(executor).forEach(perms -> {
            try {
                executorList.add(Integer.parseInt(perms.replaceFirst("controlplayer.hierarchy.", "")));
            } catch (Throwable throwable) {
                System.err.println("ControlPlayer could not understand permission of " + executor.getName() + "! (" + perms + ")");
            }
        });

        luckPermsAdapter.getHierarchyPerms(target).forEach(perms -> {
            try {
                targetList.add(Integer.parseInt(perms.replaceFirst("controlplayer.hierarchy.", "")));
            } catch (Throwable throwable) {
                System.err.println("ControlPlayer could not understand permission of " + target.getName() + "! (" + perms + ")");
            }
        });

        if(targetList.isEmpty()) return true;
        Collections.sort(executorList);
        Collections.sort(targetList);

        int executorPosition = executorList.get(0), targetPosition = targetList.get(0);
        return executorPosition > targetPosition;
    }
}
