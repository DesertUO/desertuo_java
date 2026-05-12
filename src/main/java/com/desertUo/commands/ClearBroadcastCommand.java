package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ClearBroadcastCommand implements BasicCommand {
    public static String NAME = "clearbroadcast";
    public static String DESCRIPTION = "Broadcasts a message to all players without prefix";
    public static List<String> ALIASES = List.of("cbc", "clearbc");

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(args.length == 0) {
            sender.sendMessage(Utils.formatMessage("What do I even broadcast? lol"));
            return;
        }
        String toBroadcast = String.join(" ", args);

        Bukkit.getServer().broadcast(Utils.formatMessage(toBroadcast));
        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.clearbroadcast";
    }
}
