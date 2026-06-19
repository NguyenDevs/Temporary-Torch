package com.NguyenDevs.temporaryTorch.listener;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.model.TorchRecord;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Set;
import java.util.UUID;

public class TorchPlaceListener implements Listener {
    private static final Set<Material> TORCH_TYPES = Set.of(
            Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH,
            Material.WALL_TORCH, Material.SOUL_WALL_TORCH, Material.REDSTONE_WALL_TORCH
    );

    private final TemporaryTorch plugin;

    public TorchPlaceListener(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTorchPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

        if (!TORCH_TYPES.contains(block.getType())) {
            return;
        }

        if (!player.hasPermission("temporarytorch.use")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }

        UUID playerUuid = player.getUniqueId();
        int maxTorches = plugin.getConfigManager().getMaxTorchesPerPlayer();

        if (maxTorches >= 0) {
            long currentCount = plugin.getTorchManager().getRepository().countByPlacedBy(playerUuid);
            if (currentCount >= maxTorches) {
                player.sendMessage(plugin.getConfigManager().getMessage("max-reached").replace("{max}", String.valueOf(maxTorches)));
                return;
            }
        }

        long durationMs = plugin.getConfigManager().getTorchDurationSeconds() * 1000;
        long now = System.currentTimeMillis();
        TorchRecord record = new TorchRecord(
                0,
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ(),
                playerUuid,
                now,
                now + durationMs
        );

        plugin.getTorchManager().getRepository().insert(record);

        String msg = plugin.getConfigManager().getMessage("torch-placed")
                .replace("{duration}", String.valueOf(plugin.getConfigManager().getTorchDurationSeconds()));
        player.sendMessage(msg);
    }
}
