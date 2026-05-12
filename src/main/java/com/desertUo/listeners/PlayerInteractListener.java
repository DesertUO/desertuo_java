package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.customitems.CustomItem;
import com.desertUo.managers.CustomItemManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    private DesertUo plugin = DesertUo.getPlugin();

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        plugin.getCustomItemManager().onItenract(e);

        if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player player = e.getPlayer();

            float playerYaw  = player.getLocation().getYaw();
            float playerPitch = player.getLocation().getPitch();

            float playerYawRad = (playerYaw - 90)*((float)Math.PI/180);
            //float player

            player.spawnParticle(Particle.HAPPY_VILLAGER, Utils.getRealLocationFromRelative(0, 0, 0, player.getLocation()), 3);
        }
    }
}
