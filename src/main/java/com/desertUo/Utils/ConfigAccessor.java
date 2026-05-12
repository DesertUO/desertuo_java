package com.desertUo.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigAccessor {
    private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }
        return fileConfiguration;
    }

    public void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "No se pudo guardar " + fileName, ex);
        }
    }
}
