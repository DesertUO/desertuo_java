package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.customitems.CustomItem;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public class GiveCICommand implements BasicCommand {
    public static String NAME = "giveci";
    public static String DESCRIPTION = "Gives a given custom item";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.formatMessage("&cUsage: &f/giveci <CUSTOM_ITEM_ID>"));

            return;
        }

        String id = args[0];
        CustomItem ci = plugin.getCustomItemManager().getItem(id);

        if(ci == null) {
            player.sendMessage(Utils.formatMessage("&cCustom item not found!"));
            return;
        }

        ItemStack itemToGive = ci.getItemStack(plugin);

        player.getInventory().addItem(itemToGive);
        player.sendMessage(Utils.formatMessage("&aYou received: " + id));

        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.giveci";
    }
}
