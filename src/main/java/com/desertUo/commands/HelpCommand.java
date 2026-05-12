package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.help.HelpTopic;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements BasicCommand {
    public static String NAME = "help";
    public static String DESCRIPTION = "Shows the help menu";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        String search = (args.length > 0) ? String.join(" ", args) : "";

        if(!sender.isOp() || search.equals("default")) {
            List<String> helpCommandLines = plugin.getMessagesConfig().getStringList("help-command-lines");
            for(String line: helpCommandLines) {
                sender.sendMessage(Utils.formatMessage(line));
            }
            return;
        }

        HelpTopic topic = Bukkit.getHelpMap().getHelpTopic(search);

        if (topic != null && topic.canSee(sender)) {
            sender.sendMessage(Utils.formatMessage(topic.getFullText(sender)));
        } else {
            sender.sendMessage(Utils.formatMessage("&cNo help for " + search));
        }

        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.help";
    }
}
