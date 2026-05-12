package com.desertUo.managers;

import com.desertUo.DesertUo;
import com.desertUo.customitems.ChunkAnalyzerWandCI;
import com.desertUo.customitems.CustomItem;
import com.desertUo.customitems.StaffGuiCI;
import com.desertUo.customitems.TPCompassCI;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {
    private final DesertUo plugin;

    private final Map<String, CustomItem>  ciMap = new HashMap<>();

    public CustomItemManager(DesertUo plugin) {
        this.plugin = plugin;

        register(new StaffGuiCI());
        register(new ChunkAnalyzerWandCI());
        register(new TPCompassCI());
    }

    private void register(CustomItem item) {
        ciMap.put(item.getId(), item);
    }

    public CustomItem getItem(String id) {
        return ciMap.get(id);
    }

    public void onItenract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        String itemId = CustomItem.getCustomItemId(item, plugin);

        if(itemId == null) {
            return;
        }

        CustomItem customItem = this.getItem(itemId);
        if(customItem == null) {
            return;
        }

        if((e.getAction().name().contains("RIGHT") && !customItem.getUsesRightClick()) ||
                (e.getAction().name().contains("LEFT") && !customItem.getUsesLeftClick())) {
            return;
        }

        if(e.getAction().name().contains("LEFT") && customItem.getUsesLeftClick()) {
            e.setCancelled(true);
            customItem.onLeftClick(e.getPlayer());
        } else if(e.getAction().name().contains("RIGHT") && customItem.getUsesRightClick()) {
            e.setCancelled(true);
            customItem.onRightClick(e.getPlayer());
        }

        return;
    }
}
