package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetHomeCommand implements BasicCommand {
    public static String NAME = "sethome";
    public static String DESCRIPTION = "Sets a home with a name";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        Location loc = player.getLocation();
        String homeName = (args.length == 0) ? "home" : args[0].toLowerCase();

        Document homeDoc = new Document("name", homeName)
                .append("x", loc.getX())
                .append("y", loc.getY())
                .append("z", loc.getZ());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Document data = plugin.getMongoManager().getPlayerData(player.getUniqueId());

            if (data == null) {
                player.sendMessage(Utils.formatMessage("&cError: Your player data could not be found. Try rejoining"));
                return;
            }

            // Get existing homes list
            List<Document> homes = data.getList("homes", Document.class, new ArrayList<>());

            int existingIndex = -1;
            for (int i = 0; i < homes.size(); i++) {
                if (homes.get(i).getString("name").equalsIgnoreCase(homeName)) {
                    existingIndex = i;
                    break;
                }
            }

            // Limit Check
            if (existingIndex == -1 && homes.size() >= 5) {
                player.sendMessage(Utils.formatMessage("&cYou have reached your limit of 5 homes!"));
                return;
            }

            // Update / Add
            if (existingIndex != -1) {
                homes.set(existingIndex, homeDoc); // Overwrite existing
                player.sendMessage(Utils.formatMessage("&aHome '&f" + homeName + "&a' has been updated!"));
            } else {
                homes.add(homeDoc); // Add new
                player.sendMessage(Utils.formatMessage("&aHome '&f" + homeName + "&a' has been set! (" + homes.size() + "/5)"));
            }
            plugin.getMongoManager().updatePlayerDataField(player.getUniqueId(), "homes", homes);
        });

        return;
    }
}
