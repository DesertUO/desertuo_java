package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.players.PlayerProfileCO;
import com.desertUo.customobjects.ScoreboardCO;
import com.desertUo.holder.ProfileHolder;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileCommand implements BasicCommand {
    public static String NAME = "profile";
    public static String DESCRIPTION = "Shows info of a specified player or self";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        UUID targetPlayer;
        String playerName;
        if(args.length == 0) {
            targetPlayer = player.getUniqueId();
            playerName = player.getName();
        } else {
            Player possibleOnlinePlayer = Bukkit.getPlayer(args[0]);
            if(possibleOnlinePlayer == null) {
                OfflinePlayer possibleOfflinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if(!possibleOfflinePlayer.hasPlayedBefore()) {
                    sender.sendMessage(Utils.formatMessage("&c" + args[0] +" never joined the server."));
                    return;
                }

                targetPlayer = possibleOfflinePlayer.getUniqueId();
                playerName = possibleOfflinePlayer.getName();
            } else {
                targetPlayer = possibleOnlinePlayer.getUniqueId();
                playerName = possibleOnlinePlayer.getName();
            }
        }

        // TODO
        sender.sendMessage(Utils.formatMessage("&aShowing profile GUI of: &l" + playerName + "..."));

        UUID finalTargetPlayer = targetPlayer;
        String finalPlayerName = playerName;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerProfileCO liveProfile = ScoreboardCO.getInstance().playerCache.get(finalTargetPlayer);

            int level, kills, deaths;
            long currentXp;
            long goal;
            double xpPercent;
            if (liveProfile != null) {
                level = liveProfile.level;
                kills = liveProfile.kills;
                deaths = liveProfile.deaths;
                currentXp = liveProfile.xp;
                goal = liveProfile.getXpGoal();
                xpPercent = liveProfile.getProgressPercent();
            } else {
                Document data = plugin.getMongoManager().getPlayerData(finalTargetPlayer);
                if (data == null) {
                    player.sendMessage(Utils.formatMessage("&cThis player has no data!"));
                    return;
                }
                level = data.getInteger("level", 1);
                kills = data.getInteger("kills", 0);
                deaths = data.getInteger("deaths", 0);
                currentXp = data.getLong("level-xp");
                goal = PlayerProfileCO.getXpGoal(level);
                xpPercent = 0.0;
            }

            String progressBar = Utils.getProgressBar(xpPercent, 50, "|", "&3", "&7");

            Bukkit.getScheduler().runTask(plugin, () -> {

                Inventory playerProileGUIInventory = Bukkit.createInventory(new ProfileHolder(), 9 * 6, Utils.formatMessage(finalPlayerName + "'s profile"));

                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
                if(playerHeadMeta != null) {
                    playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(finalTargetPlayer));
                    playerHeadMeta.displayName(Utils.formatMessage(finalPlayerName));

                    List<Component> playerHeadMetaLore = new ArrayList<>();
                    playerHeadMetaLore.add(Utils.formatMessage("&fKills: &c" + kills));
                    playerHeadMetaLore.add(Utils.formatMessage("&fDeaths: &9" + deaths));
                    playerHeadMetaLore.add(Utils.formatMessage(""));
                    playerHeadMetaLore.add(Utils.formatMessage("&fLevel: &b" + level + " " + progressBar + " &3" + (int) xpPercent + "%"));
                    playerHeadMetaLore.add(Utils.formatMessage("&fExperience until next level: &b" + (goal - currentXp)));

                    playerHeadMeta.lore(playerHeadMetaLore);

                    playerHead.setItemMeta(playerHeadMeta);
                }

                playerProileGUIInventory.setItem(3 * 9 + 4, playerHead);

                player.openInventory(playerProileGUIInventory);
            });
        });

        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.profile";
    }
}
