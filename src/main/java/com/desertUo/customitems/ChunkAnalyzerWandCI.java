package com.desertUo.customitems;

import com.desertUo.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChunkAnalyzerWandCI extends CustomItem {

    public ChunkAnalyzerWandCI() {
        super("CA_WAND", ChunkAnalyzerWandCI.getItemStackFormatted(), false, true);
    }

    @Override
    public void onRightClick(Player player) {
        player.performCommand("ca menu");
        // player.openInventory(new CustomInventoryHolder().getInventory());
    }

    public static ItemStack getItemStackFormatted() {
        ItemStack itemStack = new ItemStack(Material.ECHO_SHARD);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Utils.formatMessage("&cChunk Analyzer Wand"));

        List<Component> itemLore = new ArrayList<>();
        itemLore.add(Utils.formatMessage("&7Opens the chunk analyzer menu"));

        itemMeta.lore(itemLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
