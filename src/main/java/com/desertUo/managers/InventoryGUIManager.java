package com.desertUo.managers;

import com.desertUo.DesertUo;
import com.desertUo.customobjects.InventoryGUICO;
import com.desertUo.customobjects.InventoryHolderCO;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.util.HashSet;

public class InventoryGUIManager {
    private DesertUo plugin;
    private HashSet<InventoryGUICO> inventoryGUICOSet;

    public void InventoryGUIManager(DesertUo plugin) {
        this.plugin = plugin;
    }

    public void onInteract(InventoryInteractEvent e) {
        if(e.getInventory().getHolder() instanceof InventoryHolderCO) {

        }
    }
}
