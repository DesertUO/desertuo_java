package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.customobjects.ScoreboardCO;
import com.desertUo.players.PlayerProfileCO;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PlayerBreakBlockListener implements Listener {
    DesertUo plugin = DesertUo.getPlugin();
    CoreProtectAPI cp = plugin.getCoreProtect();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Material type = e.getBlock().getType();
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if (e.getBlock().hasMetadata("placed")) {
            return;
        }

        boolean isOre = type.name().contains("ORE") || type == Material.ANCIENT_DEBRIS || type == Material.SPAWNER;

        if (cp == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            CoreProtectAPI cp = plugin.getCoreProtect();
            if (cp == null) return;

            if (isOre) {
                List<String[]> lookup = cp.blockLookup(block, 0);
                if (lookup != null) {
                    for (String[] result : lookup) {
                        if (cp.parseResult(result).getActionId() == 1) return;
                    }
                }
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                PlayerProfileCO profile = ScoreboardCO.getInstance().playerCache.get(player.getUniqueId());
                if (profile == null) return;

                long xpAmount = calculateXp(type);
                profile.addXp(player, xpAmount);

                player.sendActionBar(Utils.formatMessage("&a+&2" + xpAmount + " XP &a (Mining: &f" + type.name() + "&a)"));
            });
        });
    }

    private long calculateXp(Material type) {
        return switch (type) {
            case ANCIENT_DEBRIS -> 200;
            case DIAMOND_ORE -> 100;
            case LAPIS_ORE -> 50;
            case GOLD_ORE -> 30;
            case REDSTONE_ORE -> 25;
            case IRON_ORE -> 20;
            case COAL_ORE -> 10;
            case DEEPSLATE_COAL_ORE -> 12;
            case DEEPSLATE_IRON_ORE -> 40;
            case DEEPSLATE_GOLD_ORE -> 35;
            case DEEPSLATE_LAPIS_ORE -> 55;
            case DEEPSLATE_DIAMOND_ORE -> 120;
            case SPAWNER -> 200;
            case TRIAL_SPAWNER -> 250;
            case NETHER_QUARTZ_ORE -> 15;
            case NETHER_GOLD_ORE -> 20;
            default -> 1;
        };
    }
}