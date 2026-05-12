package com.desertUo.commands;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TestParticlesCommand implements BasicCommand {
    public static String NAME = "testparticles";
    public static String DESCRIPTION = "Tests particles";
    public static List<String> ALIASES = List.of("testpart");

    private final DesertUo plugin = DesertUo.getPlugin();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        final Component name = source.getExecutor() != null ? source.getExecutor().name() : source.getSender().name();
        final CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.formatMessage("&cError. This command can only be run by players"));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(int i = 0; i < 10; i++) {
                player.spawnParticle(Particle.HAPPY_VILLAGER, Utils.getRealLocationFromRelative(0, 2, 0, player.getLocation()), 3);
            }
        });
        return;
    }

    @Override
    public @Nullable String permission() {
        return "desertuo.commands.testparticles";
    }
}
