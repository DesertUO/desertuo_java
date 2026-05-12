package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements BasicCommand {
    public static String NAME = "home";
    public static String DESCRIPTION = "Teleports to a specific home or lists all of them";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Document data = plugin.getMongoManager().getPlayerData(player.getUniqueId());
            List<Document> homes = data.getList("homes", Document.class, new ArrayList<>());

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (homes.isEmpty()) {
                    player.sendMessage(Utils.formatMessage("You have no homes set!"));
                    return;
                }

                String homeName;

                if(args.length == 0) {
                    homeName = "home";
                    // player.sendMessage(Utils.formatMessage("&You have the following homes:"));
                    // for (Document home : homes) {
                    //     String formatHomeItem = "&9- " + home.getString("name") + ": &f" + home.getDouble("x_coord") + " " + home.getDouble("y_coord") + " " + home.getDouble("z_coord");
                    //     player.sendMessage(Utils.formatMessage(formatHomeItem));
                    // }
                    // return;
                } else {
                    homeName = args[0];
                }

                boolean hasThatHome = homes.stream().anyMatch(home -> home.getString("name").equalsIgnoreCase(homeName));
                if(!hasThatHome) {
                    player.sendMessage(Utils.formatMessage("&cYou don't have any homes with that name!"));
                    return;
                }

                World overworld = Bukkit.getWorld("world");
                Document home = getHomeByName(homes, homeName);
                double[] coordinates = {home.getDouble("x"), home.getDouble("y"), home.getDouble("z")};

                Location thatHomeLocation = new Location(overworld, coordinates[0], coordinates[1], coordinates[2]);

                player.sendMessage(Utils.formatMessage("&aTeleporting to home: " + homeName + "..."));
                player.teleport(thatHomeLocation);
                return;
            });
        });
        return;

    }

    public Document getHomeByName(List<Document> homes, String searchName) {
        return homes.stream()
                .filter(home -> home.getString("name").equalsIgnoreCase(searchName))
                .findFirst()
                .orElse(null); // Returns null if no home with that name exists
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.home";
    }
}
