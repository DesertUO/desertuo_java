package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WebsiteCommand implements BasicCommand {
    public static String NAME = "website";
    public static String DESCRIPTION = "Shows the server website";
    private final DesertUo plugin = DesertUo.getPlugin();
    private final FileConfiguration messagesConfig = plugin.getMessagesConfig();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        ConfigurationSection websiteMessagesConfig = messagesConfig.getConfigurationSection("website-book-gui");

        // TODO - Make so that you can set links, custom book pages, etc
        // Not finished, not working
        String websiteBookGUITitle = "DesertUo' Website";
        String websiteBookGUIAuthor = "DesertUo";
        List<List<String>> websiteBookGUIPages = new ArrayList<>();
        List<String> genPage1 = new ArrayList<>();
        genPage1.add("");
        genPage1.add("DesertUO");
        genPage1.add("");
        genPage1.add("");
        genPage1.add("Config lacks the:");
        genPage1.add("website-book-gui");
        genPage1.add("in messages.yml");
        genPage1.add("You may regenerate");
        genPage1.add("the plugin's");
        genPage1.add("messages.yml");
        websiteBookGUIPages.add(genPage1);

        // The website book gui variables are already set on the placeholder ones
        // Do this if there is the config in messages.yml
        if(websiteMessagesConfig != null) {
            websiteBookGUITitle = websiteMessagesConfig.getString("title", "No title path");
            websiteBookGUIAuthor = websiteMessagesConfig.getString("author", "No author path");
            websiteBookGUIPages.clear();
            List<?> websiteBookGUIPagesOnConfig = websiteMessagesConfig.getList("pages", List.of(List.of("No pages path in"), List.of("path.")));
            for(Object page: websiteBookGUIPagesOnConfig) {
                // TODO - Safely add List<String> pages from unknown List<?>
                websiteBookGUIPages.add((List<String>) page);
            }
        }

        // Builds the final Book object to show to player
        Book.Builder bookGUIBuilder = Book.builder()
                .title(Utils.formatMessage(websiteBookGUITitle))
                .author(Utils.formatMessage(websiteBookGUIAuthor));

        List<Component> websiteBookGUIPagesFinal = new ArrayList<>();
        for(List<String> page: websiteBookGUIPages) {
            Component pageContent = Component.text("");
            for(String line: page) {
                pageContent = pageContent.append(Utils.formatMessage(line.concat("\n")));
            }
            websiteBookGUIPagesFinal.add(pageContent);
        }

        bookGUIBuilder.pages(websiteBookGUIPagesFinal);

        Book bookGUI = bookGUIBuilder.build();

        player.openBook(bookGUI);
    }
    @Override
    public @Nullable String permission() {
        return "desertuo.commands.website";
    }
}
