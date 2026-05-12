package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements BasicCommand {
    public static String NAME = "delhome";
    public static String DESCRIPTION = "Removes a home with a name";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final CommandSender sender = source.getSender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.formatMessage("&cUsage: /delhome <name>"));
            return;
        }

        String homeName = args[0].toLowerCase();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Document data = plugin.getMongoManager().getPlayerData(player.getUniqueId());

            if (data == null) {
                player.sendMessage(Utils.formatMessage("&cError: Player data not found. Try rejoining"));
                return;
            }

            List<Document> homes = data.getList("homes", Document.class, new ArrayList<>());

            // Try to remove the home. removeIf returns true if something was actually removed.
            boolean removed = homes.removeIf(doc -> doc.getString("name").equalsIgnoreCase(homeName));

            if (removed) {
                // Update the database with the shortened list
                plugin.getMongoManager().updatePlayerDataField(player.getUniqueId(), "homes", homes);
                player.sendMessage(Utils.formatMessage("&aHome '&f" + homeName + "&a' has been deleted."));
            } else {
                player.sendMessage(Utils.formatMessage("&cYou do not have a home named '&f" + homeName + "&c'."));
            }
        });
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.delhome";
    }
}
