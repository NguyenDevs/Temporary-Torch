package com.NguyenDevs.temporaryTorch.manager;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigManager {

    private final TemporaryTorch plugin;
    private File configFile;
    private FileConfiguration config;

    public ConfigManager(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        updateConfig();
    }

    private void updateConfig() {
        boolean updated = false;

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(plugin.getResource("config.yml"), StandardCharsets.UTF_8)
        );

        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
                updated = true;
                plugin.getLogger().info("Added missing config key: " + key);
            }
        }

        if (updated) {
            saveConfig();
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        updateConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public long getDefaultDuration() {
        return config.getLong("torch.default-duration", 600);
    }

    public int getDropStickAmount() {
        return config.getInt("torch.drops.stick.amount", 1);
    }

    public double getDropCoalChance() {
        return config.getDouble("torch.drops.coal.chance", 0.5);
    }

    public int getDropCoalAmount() {
        return config.getInt("torch.drops.coal.amount", 1);
    }

    public double getDropCharcoalChance() {
        return config.getDouble("torch.drops.charcoal.chance", 0.3);
    }

    public int getDropCharcoalAmount() {
        return config.getInt("torch.drops.charcoal.amount", 1);
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("disabled-worlds");
    }

    public boolean isWorldDisabled(String worldName) {
        return getDisabledWorlds().contains(worldName);
    }

    public int getCheckInterval() {
        return config.getInt("torch.check-interval", 20);
    }
}