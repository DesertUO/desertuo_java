package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SpawnCommand implements BasicCommand {
    public static String NAME = "spawn";
    public static String DESCRIPTION = "Teleports the player to the server spawn";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        FileConfiguration config = plugin.getConfig();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        List<Double> spawn_teleport_coordinates = config.getDoubleList("spawn-teleport-coordinates");
        String spawn_teleport_message = messagesConfig.getString("spawn-teleport-message");

        if(spawn_teleport_message != null) {
            player.sendMessage(Utils.formatMessage(spawn_teleport_message));
        }
        Location spawn_location = new Location(player.getWorld(), spawn_teleport_coordinates.get(0), spawn_teleport_coordinates.get(1), spawn_teleport_coordinates.get(2));
        player.teleport(spawn_location);

        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.spawn";
    }
}
