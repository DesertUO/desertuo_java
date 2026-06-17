package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.customobjects.ScoreboardCO;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerAsyncChatListener implements Listener {
    DesertUo plugin = DesertUo.getPlugin();
    LuckPerms lpApi = plugin.getLpApi();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMessage(AsyncChatEvent e) {
        e.setCancelled(true);

        Player player = e.getPlayer();

        /* Fetch luckperms data asynchronously */
        // TODO - Fix that on both chats it shows the prefix instead of nothing
        User lpPlayer = lpApi.getPlayerAdapter(Player.class).getUser(player);

        Component playerMessage = getPlayerMessageTextFormatted(e, lpPlayer, player).clickEvent(ClickEvent.suggestCommand("/w " + e.getPlayer().getName() + " "));

        Bukkit.getServer().broadcast(playerMessage);

        plugin.broadcastChatMessageWebSocket(getPlayerMessageTextAsHTMLFormatted(e, lpPlayer, player));
    }

    private static Component getPlayerMessageTextFormatted(AsyncChatEvent e, User lpPlayer, Player player) {
        int level = 1;
        var profile = ScoreboardCO.getInstance().playerCache.get(player.getUniqueId());
        if (profile != null) level = profile.level;

        String levelTag = "&7[&b" + level + "&7] ";

        String lpPlayerPrefix = lpPlayer.getCachedData().getMetaData().getPrefix();
        if(lpPlayerPrefix == null) lpPlayerPrefix = "";

        String lpPlayerSuffix = lpPlayer.getCachedData().getMetaData().getSuffix();
        if(lpPlayerSuffix == null) lpPlayerSuffix = "";


        final String playerPrefix = lpPlayerPrefix;
        final String playerSuffix = lpPlayerSuffix;

        Component message = e.message();

        return Utils.formatMessage(levelTag)
                .append(Utils.formatMessage(playerPrefix))
                .append(player.name())
                .append(Utils.formatMessage(playerSuffix + ": "))
                .append(message);
    }

    private static String getPlayerMessageTextAsHTMLFormatted(AsyncChatEvent e, User lpPlayer, Player player) {
        int level = 1;
        var profile = ScoreboardCO.getInstance().playerCache.get(player.getUniqueId());
        if (profile != null) level = profile.level;

        String levelTag = "[" + level + "] ";

        String lpPlayerPrefix = lpPlayer.getCachedData().getMetaData().getPrefix();
        if(lpPlayerPrefix == null) lpPlayerPrefix = "";

        String lpPlayerSuffix = lpPlayer.getCachedData().getMetaData().getSuffix();
        if(lpPlayerSuffix == null) lpPlayerSuffix = "";


        final String playerPrefix = lpPlayerPrefix;
        final String playerSuffix = lpPlayerSuffix;

        String message = PlainTextComponentSerializer.plainText().serialize(e.message());
        String safeMessage = StringEscapeUtils.escapeHtml4(message);

        StringBuilder finalString = new StringBuilder();

        finalString.append(levelTag);
        finalString.append(PlainTextComponentSerializer.plainText().serialize(Utils.formatMessage(playerPrefix)));
        finalString.append(player.getName());
        finalString.append(PlainTextComponentSerializer.plainText().serialize(Utils.formatMessage(playerSuffix)));
        finalString.append(": ");
        finalString.append(safeMessage);

        return finalString.toString();
    }
}
