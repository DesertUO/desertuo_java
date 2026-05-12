package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.players.PlayerProfileCO;
import com.desertUo.customobjects.ScoreboardCO;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class PlayerDeathListener implements Listener {
    DesertUo plugin = DesertUo.getPlugin();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killed = e.getPlayer();
        Player killer = killed.getKiller();

        UUID killedUUID = killed.getUniqueId();

        PlayerProfileCO killedProfile = ScoreboardCO.getInstance().playerCache.get(killedUUID);
        if (killedProfile != null) killedProfile.deaths++;

//        UUID killerUUID = killer != null ? killer.getUniqueId() : null;

        if (killer != null) {
            PlayerProfileCO killerProfile = ScoreboardCO.getInstance().playerCache.get(killer.getUniqueId());
            if (killerProfile != null) {
                killerProfile.kills++;

                killerProfile.addXp(killer, 100L);
                e.getPlayer().sendActionBar(Utils.formatMessage("&a+&2100 XP &a(Kill: &f" + killed.getName() +"&a)"));
            }
        }

//        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
//            Document killedData = plugin.getMongoManager().getPlayerData(killedUUID);
//            plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(killedUUID, "deaths", killedData.getInteger("deaths", 0) + 1);
//
//            if(killerUUID != null) {
//                Document killerData = plugin.getMongoManager().getPlayerData(killerUUID);
//                plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(killerUUID, "kills", killerData.getInteger("kills", 0) + 1);
//            }
//        });
    }
}
