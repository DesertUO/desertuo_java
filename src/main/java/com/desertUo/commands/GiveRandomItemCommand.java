package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiveRandomItemCommand implements BasicCommand {
    public static String NAME = "giverandomitem";
    public static String DESCRIPTION = "Gives a random item to the specified player";
    public static List<String> ALIASES = List.of("giveri");

    private final DesertUo plugin = DesertUo.getPlugin();

    List<String> exclude_items = plugin.getConfig().getStringList("gric-exclue-materials");
    List<String> exclude_contains = plugin.getConfig().getStringList("gric-exclue-contains");

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        List<Player> targets = new ArrayList<>();
        // Working on this (WIP - TODO)
        if(!(sender instanceof Player player) && (args.length == 0)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        if(args.length == 0) {
            if (!(source.getSender() instanceof Player player)) {
                source.getSender().sendMessage(Utils.formatMessage("&cConsole must specify a player: /giverandomitem <player>"));
                return;
            }
            targets.add(player);

        } else {
            String selector = args[0];

            if (selector.equalsIgnoreCase("@a")) {
                targets.addAll(Bukkit.getOnlinePlayers());
            } else if (selector.equalsIgnoreCase("@r")) {
                List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (!online.isEmpty()) {
                    targets.add(online.get(new Random().nextInt(online.size())));
                }
            } else {
                Player p = Bukkit.getPlayerExact(selector);
                if (p != null) targets.add(p);
            }
        }

        if (targets.isEmpty()) {
            source.getSender().sendMessage(Utils.formatMessage("&cNo players found."));
            return;
        }

        for(Player target : targets) {
            Inventory playerInventory = target.getInventory();
            ItemStack randomItem = Utils.getRandomMaterialItem(exclude_items, exclude_contains);

            playerInventory.addItem(randomItem);
            String itemName = randomItem.getType().name().toLowerCase().replace("_", " ");
            sender.sendMessage(Utils.formatMessage("You've given a &b" + itemName + " to " + target.getName()));
            target.sendMessage(Utils.formatMessage("You have received a &b" + itemName + "&r!"));
        }

        return;
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        // It worked for a day, IDK
        
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("@a");
            suggestions.add("@r");

            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }

            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.giverandomitem";
    }
}
