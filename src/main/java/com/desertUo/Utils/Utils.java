package com.desertUo.Utils;

import com.desertUo.DesertUo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils {
    private static final DesertUo plugin = DesertUo.getPlugin();
    private static final LuckPerms lpApi = DesertUo.getPlugin().getLpApi();

    public static Component formatMessage(String msg) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
    }

    public static String unformatMessage(Component msg) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(msg);
    }

    public static Component[] splitLineWithFormatting(Component lineComp) {
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        String line = unformatMessage(lineComp);

        String validCodes = "0123456789abcdefklmnor";

        boolean hasReset = false; // Flag to track if a reset has occurred

        // Iterate over the string character by character
        int length = line.length();
        int splitIndex = 16;  // Set the split point after 16 characters

        List<String> codes = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            char currentChar = line.charAt(i);

            // If we find a color/style code
            if (currentChar == '&' && i + 1 < length) {
                char nextChar = line.charAt(i + 1);


                if (validCodes.indexOf(nextChar) != -1) {
                    String code = "&" + String.valueOf(nextChar); // Get the color or style code

                    // If we encounter a reset, handle it separately
                    if (code.equals("&r")) {
                        hasReset = true;
                    } else {
                        hasReset = false;
                        codes.add(code);
                    }

                    // Append the code to prefix and suffix
                    if (i < splitIndex) {
                        prefix.append(code);
                    } else {
                        suffix.append(code);
                    }

                    // Skip the next character (since we processed a 2-char code)
                    i++;
                } else {
                    // It's not a valid color or style code, just copy the character
                    if (i < splitIndex) {
                        prefix.append(currentChar);
                    } else {
                        suffix.append(currentChar);
                    }
                }
            } else {
                // Just copy the character
                if (i < splitIndex) {
                    prefix.append(currentChar);
                } else {
                    suffix.append(currentChar);
                }
            }
        }

        // Handle if suffix needs to be prefixed with last applied format
        if (!hasReset && !codes.isEmpty()) {
            if(codes.size() > 1) {
                suffix.insert(0, codes.get(codes.size()-2));
            }
            suffix.insert(0, codes.getLast());
        }

        Component finalPreffix = formatMessage(prefix.toString());
        Component finalSuffix = formatMessage(suffix.toString());

        return new Component[] { finalPreffix, finalSuffix };
    }

    public static Location getRealLocationFromRelative(double rel_x, double rel_y, double rel_z, Location location) {
        return new Location(location.getWorld(), location.getX() + rel_x, location.getY() + rel_y, location.getZ() + rel_z);
    }

    public static ItemStack getRandomMaterialItem(List<String> exclude_items, List<String> exclude_contains) {
        Material[] materials = Material.values();

        Random random = new Random();

        Material randomMaterial;

        do {
            randomMaterial = materials[new Random().nextInt(materials.length)];
        } while(!randomMaterial.isItem() ||
                randomMaterial.isAir() ||
                randomMaterial.name().startsWith("LEGACY") ||
                exclude_items.contains(randomMaterial.name())
        );

        return new ItemStack(randomMaterial);
    }

    public static void setPlayerPermission(UUID playerUUID, String permission, boolean value) {
        // 1. Load the user from the LuckPerms DataStore
        lpApi.getUserManager().modifyUser(playerUUID, user -> {

            // 2. Create the Node (the permission object)
            Node node = Node.builder(permission).value(value).build();

            // 3. Add the node to the user's data
            DataMutateResult result = user.data().add(node);

            if (result.wasSuccessful()) {
                plugin.getLogger().info("Set " + permission + " to " + value + " for " + playerUUID);
            }
        });
    }

    public static String getProgressBar(double percent, int totalBars, String symbol, String completedColor, String remainingColor) {
        int completedBars = (int) (totalBars * (percent / 100));
        int remainingBars = totalBars - completedBars;

        // COMPLETED COLOR (|) REMAINING COLOR (:) as in |||||||||||::::
        return completedColor + symbol.repeat(Math.max(0, completedBars)) +
                remainingColor + symbol.repeat(Math.max(0, remainingBars));
    }
}
