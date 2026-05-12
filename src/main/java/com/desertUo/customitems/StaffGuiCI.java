package com.desertUo.customitems;

import com.desertUo.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StaffGuiCI extends CustomItem {

    public StaffGuiCI() {
        super("STAFF_GUI_WAND", StaffGuiCI.getItemStackFormatted(), false, true);
    }

    @Override
    public void onRightClick(Player player) {
        player.sendMessage(Utils.formatMessage("&aOpening staff GUI"));
        // player.openInventory(new CustomInventoryHolder().getInventory());
    }

    public static ItemStack getItemStackFormatted() {
        ItemStack itemStack = new ItemStack(Material.COPPER_BLOCK);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Utils.formatMessage("&aStaff Gui Wand"));

        List<Component> itemLore = new ArrayList<>();
        itemLore.add(Utils.formatMessage("&7Opens the Staff GUI"));

        itemMeta.lore(itemLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
