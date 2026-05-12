package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.KitUtils;
import com.desertUo.Utils.PlaytimeUtils;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class StarterCommand implements BasicCommand {
    public static String NAME = "starter";
    public static String DESCRIPTION = "Gives the starter kit to the player";

    private final DesertUo plugin = DesertUo.getPlugin();
    HashMap<UUID, Long> playerCooldowns;

    public StarterCommand() {
        this.playerCooldowns = new HashMap<>();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        FileConfiguration config = plugin.getConfig();
        long playerCooldownMillis = config.getLong("starter-command-cooldown");

        if(!this.playerCooldowns.containsKey(player.getUniqueId())) {
            this.playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

            KitUtils.handleKitClaim(player, false);
            return;
        } else {
            // Millis difference
            long timeElapsed = System.currentTimeMillis() - this.playerCooldowns.get(player.getUniqueId());

            if(timeElapsed >= playerCooldownMillis) {
                playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

                KitUtils.handleKitClaim(player, false);
                return;
            }

            player.sendMessage(Utils.formatMessage("&cYou cannot claim the starter kit again... &9Cooldown: &b" + PlaytimeUtils.getStringFormattedMillis(playerCooldownMillis - timeElapsed)));
        }
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.starter";
    }
}
