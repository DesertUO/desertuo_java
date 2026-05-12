package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.holder.ProfileHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.util.UUID;

public class InventoryActionsListener implements Listener {
    DesertUo plugin = DesertUo.getPlugin();

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        if(e.getInventory().getHolder() instanceof ProfileHolder) {
            e.setCancelled(true);
            playerCLickInventoryCallback((Player) e.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getHolder() instanceof ProfileHolder) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();

            if (e.getClick().isKeyboardClick()) {
                e.setCancelled(true);
                player.updateInventory(); // Force sync immediately
                return;
            }

            playerCLickInventoryCallback((Player) e.getWhoClicked());

            plugin.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
    }

    @EventHandler
    public void onInventorydDrag(InventoryDragEvent e) {
        if(e.getInventory().getHolder() instanceof ProfileHolder) {
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();
            plugin.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(e.getInventory().getHolder() instanceof ProfileHolder) {
            ((Player) e.getPlayer()).updateInventory();
        }
    }

    private void playerCLickInventoryCallback(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();
    }

}
