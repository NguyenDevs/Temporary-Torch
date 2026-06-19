package com.NguyenDevs.temporaryTorch.scheduler;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.database.TorchRepository;
import com.NguyenDevs.temporaryTorch.model.TorchRecord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class TorchExpiryScheduler extends BukkitRunnable {
    private static final long CHECK_INTERVAL_TICKS = 100;
    private static final Set<Material> TORCH_TYPES = Set.of(
            Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH,
            Material.WALL_TORCH, Material.SOUL_WALL_TORCH, Material.REDSTONE_WALL_TORCH
    );

    private final TemporaryTorch plugin;
    private final TorchRepository repository;

    public TorchExpiryScheduler(TemporaryTorch plugin, TorchRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        List<TorchRecord> expired = repository.findExpired(now);

        for (TorchRecord record : expired) {
            World world = Bukkit.getWorld(record.world());
            if (world == null) {
                repository.deleteById(record.id());
                continue;
            }

            Location loc = new Location(world, record.x(), record.y(), record.z());
            Material type = loc.getBlock().getType();

            if (TORCH_TYPES.contains(type)) {
                loc.getBlock().setType(Material.AIR);
            }

            repository.deleteById(record.id());
        }
    }

    public void start() {
        runTaskTimer(plugin, CHECK_INTERVAL_TICKS, CHECK_INTERVAL_TICKS);
    }
}
