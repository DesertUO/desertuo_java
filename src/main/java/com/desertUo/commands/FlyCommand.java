package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class FlyCommand implements BasicCommand {
    public static String NAME = "fly";
    public static String DESCRIPTION = "Toggles flight mode for player";

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }

        if(player.getAllowFlight()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            plugin.playersToggledFlight.remove(player);
            player.sendMessage(Utils.formatMessage("&aFlying disabled."));
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            plugin.playersToggledFlight.add(player);
            player.sendMessage(Utils.formatMessage("&aFlying enabled."));
        }
        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.fly";
    }
}
