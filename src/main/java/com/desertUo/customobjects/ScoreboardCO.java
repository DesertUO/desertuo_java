package com.desertUo.customobjects;

import com.desertUo.DesertUo;
import com.desertUo.Utils.PlaytimeUtils;
import com.desertUo.Utils.Utils;
import com.desertUo.players.PlayerProfileCO;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardCO implements Runnable {
    private final static DesertUo plugin = DesertUo.getPlugin();
    private final static ScoreboardCO instance = new ScoreboardCO();

    LuckPerms lpApi = plugin.getLpApi();

    public final ConcurrentHashMap<UUID, Scoreboard> playerScoreboards = new ConcurrentHashMap<>();
    public Map<UUID, PlayerProfileCO> playerCache = new HashMap<>();

    private ScoreboardCO() {}

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfileCO profile = playerCache.get(player.getUniqueId());
            if(profile != null) {
                profile.tickPlaytime(player);
            }

            if(playerScoreboards.containsKey(player.getUniqueId())) {
                updateScoreboard(player);
            } else {
                createNewScoreboard(player);
            }
        }
    }

    public Scoreboard getPlayerScoreboard(UUID uuid) {
        return playerScoreboards.get(uuid);
    }

    public void createNewScoreboard(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerUUIDStr = playerUUID.toString();

        PlayerProfileCO profile = playerCache.get(playerUUID);
        if (profile == null) {
            plugin.getLogger().warning(player.getName() + " seems to not have a profile cached!");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User lpPlayer = lpApi.getPlayerAdapter(Player.class).getUser(player);

            String lpPlayerPrefix = lpPlayer.getCachedData().getMetaData().getPrefix();
            if(lpPlayerPrefix == null) lpPlayerPrefix = "";

            String lpPlayerSuffix = lpPlayer.getCachedData().getMetaData().getSuffix();
            if(lpPlayerSuffix == null) lpPlayerSuffix = "";

            final String playerPrefix = lpPlayerPrefix;
            final String playerSuffix = lpPlayerSuffix;

            Document data = plugin.getMongoManager().getPlayerData(playerUUID);

            /* Start assigning new team to player for showing its nametag on the main thread */
            Bukkit.getScheduler().runTask(plugin, () -> {
                Scoreboard playerScoreboard = getAndCreateIfNullPlayerScoreboard(playerUUID);

                Component sidebarTitle = Utils.formatMessage("&e&lSurvival");

                // FIX: Check if "sidebar" exists. If it does, unregister it or reuse it.
                Objective playerSidebar = playerScoreboard.getObjective("sidebar");
                if (playerSidebar != null) {
                    playerSidebar.unregister();
                }

                playerSidebar = playerScoreboard.registerNewObjective("sidebar", Criteria.DUMMY, sidebarTitle, RenderType.INTEGER);

                playerSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
                playerSidebar.displayName(sidebarTitle);
                playerSidebar.numberFormat(NumberFormat.blank());

                Team playerTeam = playerScoreboard.getTeam(playerUUIDStr);
                if (playerTeam == null) {
                    playerTeam = playerScoreboard.registerNewTeam(playerUUIDStr);
                }

                playerTeam.addEntry(player.getName());

                playerTeam.prefix(Utils.formatMessage(playerPrefix));
                playerTeam.suffix(Utils.formatMessage(playerSuffix));

                // Add things like sidebar, etc.

                LocalDateTime currentDateTime = LocalDateTime.now();
                String pattern = "dd/MM/yy";
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
                String formattedDateTime = currentDateTime.format(dateFormatter);
                Component playerLine15 = Utils.formatMessage("&7" + formattedDateTime);
                setPlayerScoreboardSidebarLine(playerUUID, playerLine15, 15);

                Component playerLine14 = Component.text("");
                setPlayerScoreboardSidebarLine(playerUUID, playerLine14, 14);

                // Component playerLine13 = Utils.formatMessage("Player: &b").append(player.name());
                // setPlayerScoreboardSidebarLine(playerUUID, playerLine13, 13);

                Component playerLine12 = Utils.formatMessage("Playtime: &e" + PlaytimeUtils.getStringFormattedPlaytime(player));
                setPlayerScoreboardSidebarLine(playerUUID, playerLine12, 12);

                Component playerLine11 = Utils.formatMessage("Level: &b" + data.getInteger("level") + " &7(" + data.getLong("level-xp") + " / " + data.getLong("level-xp-goal") + "xp)");
                setPlayerScoreboardSidebarLine(playerUUID, playerLine11, 11);

                Component playerLine10 = Utils.formatMessage("Kills: &c" + data.getInteger("kills"));
                setPlayerScoreboardSidebarLine(playerUUID, playerLine10, 10);

                Component playerLine9 = Utils.formatMessage("Deaths: &9" + data.getInteger("deaths"));
                setPlayerScoreboardSidebarLine(playerUUID, playerLine9, 9);

                Component playerLine8 = Utils.formatMessage("Players: &b" + Bukkit.getOnlinePlayers().size());
                setPlayerScoreboardSidebarLine(playerUUID, playerLine8, 8);

                Component playerLine1 = Utils.formatMessage("");
                setPlayerScoreboardSidebarLine(playerUUID, playerLine1, 1);

                Component sidebarFooter = Utils.formatMessage("&ewww.desertuo.com");
                setPlayerScoreboardSidebarLine(playerUUID, sidebarFooter, 0);

                player.setScoreboard(playerScoreboard);
            });
        });
    }

    public void setPlayerScoreboardSidebarLine(UUID uuid, Component s, int line) {
        Scoreboard playerScoreboard = this.getAndCreateIfNullPlayerScoreboard(uuid);

        int ln1 = line / 10;
        int ln2 = line % 10;

        String lineID = ("§" + ln1 + "§" + ln2);

        Team lineTeam;
        Team playerScoreboardLineTeam = playerScoreboard.getTeam("sidebarLine" + line);
        lineTeam = Objects.requireNonNullElseGet(playerScoreboardLineTeam, () -> playerScoreboard.registerNewTeam("sidebarLine" + line));

        if(!lineTeam.hasEntry(lineID)) {
            lineTeam.addEntry(lineID);
        }

        Component[] brokenFullText = Utils.splitLineWithFormatting(s);
        Component prefix = brokenFullText[0];
        Component suffix = brokenFullText[1];

        lineTeam.prefix(prefix);
        lineTeam.suffix(suffix);

        playerScoreboard.getObjective("sidebar").getScore(lineID).setScore(line);
    }

    public Scoreboard getAndCreateIfNullPlayerScoreboard(UUID uuid) {
        Scoreboard playerSupposedScoreboard = this.getPlayerScoreboard(uuid);
        Scoreboard playerScoreboard = Objects.requireNonNullElseGet(playerSupposedScoreboard, () -> plugin.getServer().getScoreboardManager().getNewScoreboard());

        playerScoreboards.putIfAbsent(uuid, playerScoreboard);

        return playerScoreboard;
    }

    public void updateScoreboard(Player player) {
        UUID playerUUID = player.getUniqueId();

        PlayerProfileCO profile = playerCache.get(player.getUniqueId());
        if (profile == null) return;

        Component playerLine12 = Utils.formatMessage("Playtime: &e" + PlaytimeUtils.getStringFormattedPlaytime(player));
        setPlayerScoreboardSidebarLine(playerUUID, playerLine12, 12);

        Component playerLine11 = Utils.formatMessage("Level: &b" + profile.level + " &7(" + profile.xp + " xp / " + profile.getXpGoal() + " xp)");
        setPlayerScoreboardSidebarLine(playerUUID, playerLine11, 11);

        Component playerLine10 = Utils.formatMessage("Kills: &c" + profile.kills);
        setPlayerScoreboardSidebarLine(playerUUID, playerLine10, 10);

        Component playerLine9 = Utils.formatMessage("Deaths: &9" + profile.deaths);
        setPlayerScoreboardSidebarLine(playerUUID, playerLine9, 9);

        Component playerLine8 = Utils.formatMessage("Players: &b" + Bukkit.getOnlinePlayers().size());
        setPlayerScoreboardSidebarLine(playerUUID, playerLine8, 8);
    }

    public static ScoreboardCO getInstance() {
        return instance;
    }
}
