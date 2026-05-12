package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomesCommand implements BasicCommand {
    public static String NAME = "homes";
    public static String DESCRIPTION = "Lists all of the player's homes";

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

                player.sendMessage(Utils.formatMessage("&bYou have the following homes:"));
                for (Document home : homes) {
                    String formatHomeItem = "&9- " + home.getString("name") + ": &f" + home.getDouble("x") + " " + home.getDouble("y") + " " + home.getDouble("z");
                     player.sendMessage(Utils.formatMessage(formatHomeItem));
                }
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
