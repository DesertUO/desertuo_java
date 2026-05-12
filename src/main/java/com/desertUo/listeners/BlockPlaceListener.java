package com.desertUo.listeners;

import com.desertUo.DesertUo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPlaceListener implements Listener {
    DesertUo plugin = DesertUo.getPlugin();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        return;
    }
}
