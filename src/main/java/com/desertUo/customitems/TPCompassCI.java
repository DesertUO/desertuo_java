package com.desertUo.customitems;

import com.desertUo.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TPCompassCI extends CustomItem{

    public TPCompassCI() {
        super("TP_COMPASS", TPCompassCI.getItemStackFormatted(), true, true);
    }

    public static ItemStack getItemStackFormatted() {
        ItemStack itemStack = new ItemStack(Material.COMPASS);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Utils.formatMessage("&fTp Compass"));

        List<Component> itemLore = new ArrayList<>();
        itemLore.add(Utils.formatMessage("&7Teleports where the player pointss"));

        itemMeta.lore(itemLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
