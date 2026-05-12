package com.desertUo.Utils;

import com.desertUo.DesertUo;
import net.luckperms.api.LuckPerms;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KitUtils {
    public static void giveStarterKit(Player player) {

        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
        player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
        player.getInventory().addItem(new ItemStack(Material.STONE_SHOVEL));

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemStack[] armor = {boots, leggings, chestplate, helmet};

        player.getInventory().setArmorContents(armor);
    }

    public static void handleKitClaim(Player player, boolean removePermission) {
        UUID uuid = player.getUniqueId();
        LuckPerms lpApi = DesertUo.getPlugin().getLpApi();

        boolean canClaim = player.hasPermission("desertuo.commands.starter");

        if (canClaim) {
            KitUtils.giveStarterKit(player);
            player.sendMessage(Utils.formatMessage("&aStarter kit claimed!"));

            if(removePermission)
                Utils.setPlayerPermission(uuid, "desertuo.commands.starter", false);
        } else {
            player.sendMessage(Utils.formatMessage("&cYou have already claimed your starter kit!"));
        }
    }
}
