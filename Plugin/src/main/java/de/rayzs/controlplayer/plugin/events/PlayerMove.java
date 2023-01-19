package de.rayzs.controlplayer.plugin.events;

import com.sun.scenario.effect.impl.sw.java.JSWEffectPeer;
import de.rayzs.controlplayer.api.control.ControlInstance;
import de.rayzs.controlplayer.api.control.ControlManager;
import de.rayzs.controlplayer.api.control.ControlSwap;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        /*
        player.sendMessage("§" + new Random().nextInt(10) + "#######");

        ControlInstance instance = ControlManager.getControlInstance(player);
        if(instance == null) return;

        ControlSwap swap = ControlManager.getControlSwap(instance);
        if(swap == null) return;

        int instanceState = ControlManager.getInstanceState(player);

        swap.checkAndSwap(instanceState == 0
                ? ControlSwap.PlayerType.CONTROLLER
                : ControlSwap.PlayerType.VICTIM);
         */
    }
}
