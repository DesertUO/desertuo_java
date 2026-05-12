package com.desertUo.customitems;

import com.desertUo.DesertUo;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomItem {
    private final String id;
    private final ItemStack itemStack;
    private final boolean usesLeftClick;
    private final boolean usesRightClick;

    public CustomItem(String id, ItemStack itemStack, boolean usesLeftClick, boolean usesRightClick) {
        this.id = id;
        this.itemStack = itemStack;
        this.usesLeftClick = usesLeftClick;
        this.usesRightClick = usesRightClick;
    }

    public String getId() {
        return this.id;
    }

    public boolean getUsesLeftClick() {
        return usesLeftClick;
    }

    public boolean getUsesRightClick() {
        return usesRightClick;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    // This is where you define what happens on each click
    public void onLeftClick(Player player) {};
    public void onRightClick(Player player) {};

    public ItemStack getItemStack(DesertUo plugin) {
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "item_id"),
                PersistentDataType.STRING,
                id
        );
        item.setItemMeta(meta);
        return item;
    }

    public static String getCustomItemId(ItemStack item, DesertUo plugin) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return null;
        }

        NamespacedKey key = new NamespacedKey(plugin, "item_id");
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            return container.get(key, PersistentDataType.STRING);
        }

        return null; // No custom ID found
    }
}
