package com.NguyenDevs.temporaryTorch.manager;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.models.TorchData;
import com.NguyenDevs.temporaryTorch.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TorchManager {

    private final TemporaryTorch plugin;
    private final Map<String, TorchData> torches;
    private File dataFile;
    private FileConfiguration data;
    private BukkitTask decayTask;
    private final Random random;

    public TorchManager(TemporaryTorch plugin) {
        this.plugin = plugin;
        this.torches = new HashMap<>();
        this.random = new Random();
    }

    public void loadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
                return;
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);

        if (data.contains("torches")) {
            for (String key : data.getConfigurationSection("torches").getKeys(false)) {
                try {
                    String path = "torches." + key;
                    String[] coords = key.split(",");

                    String worldName = coords[0];
                    int x = Integer.parseInt(coords[1]);
                    int y = Integer.parseInt(coords[2]);
                    int z = Integer.parseInt(coords[3]);

                    Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
                    long placedTime = data.getLong(path + ".placed-time");
                    long duration = data.getLong(path + ".duration");
                    String ownerUUID = data.getString(path + ".owner", "");

                    TorchData torchData = new TorchData(location, placedTime, duration, ownerUUID);
                    torches.put(key, torchData);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load torch data for key: " + key);
                }
            }
        }

        plugin.getLogger().info("Loaded " + torches.size() + " torches from data.yml");
    }

    public void saveData() {
        data = new YamlConfiguration();

        for (Map.Entry<String, TorchData> entry : torches.entrySet()) {
            String key = entry.getKey();
            TorchData torchData = entry.getValue();
            String path = "torches." + key;

            data.set(path + ".placed-time", torchData.getPlacedTime());
            data.set(path + ".duration", torchData.getDuration());
            data.set(path + ".owner", torchData.getOwnerUUID());
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public void addTorch(Location location, Player player) {
        String key = getLocationKey(location);
        long duration = PermissionUtils.getTorchDuration(player, plugin);

        TorchData torchData = new TorchData(
                location,
                System.currentTimeMillis(),
                duration,
                player.getUniqueId().toString()
        );

        torches.put(key, torchData);
    }

    public void removeTorch(Location location) {
        String key = getLocationKey(location);
        torches.remove(key);
    }

    public boolean hasTorch(Location location) {
        String key = getLocationKey(location);
        return torches.containsKey(key);
    }

    public TorchData getTorch(Location location) {
        String key = getLocationKey(location);
        return torches.get(key);
    }

    public void startDecayTask() {
        int interval = plugin.getConfigManager().getCheckInterval();

        decayTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            checkExpiredTorches();
        }, interval, interval);
    }

    public void stopDecayTask() {
        if (decayTask != null) {
            decayTask.cancel();
        }
    }

    private void checkExpiredTorches() {
        torches.entrySet().removeIf(entry -> {
            TorchData torchData = entry.getValue();

            if (torchData.isExpired()) {
                Location location = torchData.getLocation();

                if (location.getBlock().getType() == Material.TORCH ||
                        location.getBlock().getType() == Material.WALL_TORCH) {

                    location.getBlock().setType(Material.AIR);

                    dropItems(location);
                }

                return true;
            }

            return false;
        });
    }

    private void dropItems(Location location) {
        int stickAmount = plugin.getConfigManager().getDropStickAmount();
        if (stickAmount > 0) {
            location.getWorld().dropItemNaturally(location, new ItemStack(Material.STICK, stickAmount));
        }
        double coalChance = plugin.getConfigManager().getDropCoalChance();
        if (random.nextDouble() < coalChance) {
            int coalAmount = plugin.getConfigManager().getDropCoalAmount();
            if (coalAmount > 0) {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.COAL, coalAmount));
            }
        }
        double charcoalChance = plugin.getConfigManager().getDropCharcoalChance();
        if (random.nextDouble() < charcoalChance) {
            int charcoalAmount = plugin.getConfigManager().getDropCharcoalAmount();
            if (charcoalAmount > 0) {
                ItemStack charcoal = new ItemStack(Material.COAL, charcoalAmount);
                charcoal.setDurability((short) 1); // Charcoal
                location.getWorld().dropItemNaturally(location, charcoal);
            }
        }
    }

    public void dropItemsManually(Location location) {
        dropItems(location);
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }
}