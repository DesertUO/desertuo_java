    package com.desertUo.listeners;

    import com.desertUo.DesertUo;
    import com.desertUo.Utils.Utils;
    import com.desertUo.players.PlayerProfileCO;
    import com.desertUo.customobjects.ScoreboardCO;
    import net.kyori.adventure.text.Component;
    import net.kyori.adventure.title.Title;
    import net.luckperms.api.LuckPerms;
    import org.bson.Document;
    import org.bukkit.Bukkit;
    import org.bukkit.Sound;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.player.PlayerJoinEvent;
    import org.bukkit.event.player.PlayerQuitEvent;
    import org.bukkit.scoreboard.Scoreboard;
    import org.bukkit.scoreboard.Team;

    import java.util.UUID;

    public class PlayerJoinQuitListener implements Listener {
        DesertUo plugin = DesertUo.getPlugin();
        LuckPerms lpApi = plugin.getLpApi();

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            e.joinMessage(Utils.formatMessage("&7[&a+&7]&r ").append(e.getPlayer().name()));

            /* Player data */
            Player player = e.getPlayer();
            UUID playerUUID = player.getUniqueId();
            String playerName = player.getName();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Document data = plugin.getMongoManager().getPlayerData(playerUUID);

                if(data == null) {
                    plugin.getMongoManager().savePlayerData(playerUUID, playerName);
                    plugin.getLogger().info("Created new profile for " + playerName);

                    data = plugin.getMongoManager().getPlayerData(playerUUID);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        welcomePlayerPhase(player);
                    });
                } else {
                    plugin.getMongoManager().updatePlayerDataField(playerUUID, "last_login", System.currentTimeMillis());
                }

                PlayerProfileCO profile = new PlayerProfileCO(data);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    ScoreboardCO.getInstance().playerCache.put(playerUUID, profile);
                    if(!ScoreboardCO.getInstance().playerScoreboards.containsKey(player.getUniqueId())) {
                        ScoreboardCO.getInstance().createNewScoreboard(player);
                    } else {
                        player.setScoreboard(ScoreboardCO.getInstance().getPlayerScoreboard(player.getUniqueId()));
                    }
                });
            });

            // Restore flying if toggled before quiting
            if(plugin.playersToggledFlight.contains(player)) {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(Utils.formatMessage("&bFlying restored from previous session."));
            }
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e) {
            e.quitMessage(Utils.formatMessage("&7[&c-&7]&r ").append(e.getPlayer().name()));

            Player player = e.getPlayer();
            UUID playerUUID = player.getUniqueId();
            String playerUUIDStr = playerUUID.toString();
            Scoreboard playerNameTagsScoreboard = ScoreboardCO.getInstance().getAndCreateIfNullPlayerScoreboard(playerUUID);
            Team playerTeam = playerNameTagsScoreboard.getTeam(playerUUIDStr);
            if (playerTeam != null) {
                playerTeam.removeEntry(player.getName());
                playerTeam.unregister();
            }

            PlayerProfileCO profile = ScoreboardCO.getInstance().playerCache.remove(playerUUID);

            if (profile != null) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    // Ignore this one xdd (Implement later cause IDK what will actually be the full form of the player doc )
                    Document doc = new Document()
                            .append("name", e.getPlayer().getName())
                            .append("level", profile.getLevel())
                            .append("level-xp", profile.getXp())
                            .append("kills", profile.getKills())
                            .append("deaths", profile.getDeaths())
                            .append("last_login", System.currentTimeMillis());
                    plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(playerUUID, "kills", profile.kills);
                    plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(playerUUID, "deaths", profile.deaths);
                    plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(playerUUID, "level", profile.level);
                    plugin.getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(playerUUID, "level-xp", profile.xp);
                });
            }
        }

        public void welcomePlayerPhase(Player player) {
            Component titleTitle = Utils.formatMessage("&3&lWELCOME!");
            Component titleSubtitle = Utils.formatMessage("&fDo &a/starter&f to start your journey!");
            Title title = Title.title(titleTitle, titleSubtitle);
            player.showTitle(title);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            Bukkit.getServer().broadcast(Utils.formatMessage("&b&l" + player.getName() + "&3 joined for the first time! &9#" + Bukkit.getOfflinePlayers().length));
            Bukkit.getServer().broadcast(Utils.formatMessage("&3Please &9welcome&3 the new player! :)"));

            player.sendMessage(Utils.formatMessage("Don't leave please &e:3"));
            player.sendActionBar(Utils.formatMessage("&fDon't forget to &a/fav &fthis server :)"));

            // To Do
        }
    }
