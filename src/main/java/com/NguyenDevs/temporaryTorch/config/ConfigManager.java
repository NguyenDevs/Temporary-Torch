package com.NguyenDevs.temporaryTorch.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigManager {
    private static final String TORCH_DURATION = "torch-duration-seconds";
    private static final String MAX_PER_PLAYER = "max-torches-per-player";
    private static final String REFUEL_COAL = "refuel.coal-seconds";
    private static final String REFUEL_CHARCOAL = "refuel.charcoal-seconds";
    private static final String REFUEL_ALLOW_OTHERS = "refuel.allow-others-refuel";
    private static final String REFUEL_MAX_LIFETIME = "refuel.max-torch-lifetime-seconds";

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reload() {
        load();
    }

    public long getTorchDurationSeconds() {
        return config.getLong(TORCH_DURATION, 300);
    }

    public int getMaxTorchesPerPlayer() {
        return config.getInt(MAX_PER_PLAYER, 10);
    }

    public long getRefuelCoalSeconds() {
        return config.getLong(REFUEL_COAL, 60);
    }

    public long getRefuelCharcoalSeconds() {
        return config.getLong(REFUEL_CHARCOAL, 30);
    }

    public boolean isAllowOthersRefuel() {
        return config.getBoolean(REFUEL_ALLOW_OTHERS, false);
    }

    public long getMaxTorchLifetimeSeconds() {
        return config.getLong(REFUEL_MAX_LIFETIME, 3600);
    }

    public String getMessage(String path) {
        String msg = config.getString("messages." + path);
        if (msg == null) {
            return ChatColor.RED + "Message not found: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
