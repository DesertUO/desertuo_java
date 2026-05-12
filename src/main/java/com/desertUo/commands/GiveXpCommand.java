package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.desertUo.customobjects.ScoreboardCO;
import com.desertUo.players.PlayerProfileCO;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GiveXpCommand implements BasicCommand {
    public static String NAME = "givexp";
    public static String DESCRIPTION = "Gives experience to a player";
    public static List<String> ALIASES = List.of("addxp");

    private final DesertUo plugin = DesertUo.getPlugin();

    private List<String> cachedNames = new ArrayList<>();
    private long lastUpdate = 0;

    public GiveXpCommand() {
        super();
        this.updateCache();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        if(args.length < 1) {
            player.sendMessage(Utils.formatMessage("&cPlease specify a player"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(Utils.formatMessage("&cPlease provide an experience amount"));
            return;
        }

        String targetName = args[0];
        boolean existsLocally = Bukkit.getPlayer(targetName) != null ||
                cachedNames.stream().anyMatch(target -> target.equalsIgnoreCase(targetName));
        if (!existsLocally) {
            sender.sendMessage(Utils.formatMessage("&cPlayer not found in our records!"));
            return;
        }

        long xpAmount;
        try {
            xpAmount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.formatMessage("&cInvalid amount! Please enter a number."));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer recipient = Bukkit.getOfflinePlayer(targetName);

            PlayerProfileCO profile = ScoreboardCO.getInstance().playerCache.get(recipient.getUniqueId());
            if(profile == null) {
                return;
            }
            profile.addXp(player, xpAmount);

            if (recipient.isOnline() && recipient.getPlayer() != null) {
                recipient.getPlayer().sendMessage(Utils.formatMessage("&2" + xpAmount + " XP &ahas been added to your stats!"));
            }
        });

        return;
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 1) {
            long now = System.currentTimeMillis();

            // Update cache every 10 minutes (600,000 ms)
            if (now - lastUpdate > 600_000 || cachedNames.isEmpty()) {
                this.updateCache();
            }

            String input = args[0].toLowerCase();
            return cachedNames.stream()
                .filter(name -> name.toLowerCase().startsWith(input))
                .limit(20)
                .toList();
        }
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.givexp";
    }

    private void updateCache() {
        this.cachedNames = Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(Objects::nonNull)
            .toList();
        this.lastUpdate = System.currentTimeMillis();
    }
}
