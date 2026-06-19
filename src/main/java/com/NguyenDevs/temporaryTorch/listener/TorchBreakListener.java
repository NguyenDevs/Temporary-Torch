package com.NguyenDevs.temporaryTorch.listener;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class TorchBreakListener implements Listener {
    private static final Set<Material> TORCH_TYPES = Set.of(
            Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH,
            Material.WALL_TORCH, Material.SOUL_WALL_TORCH, Material.REDSTONE_WALL_TORCH
    );

    private final TemporaryTorch plugin;

    public TorchBreakListener(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTorchBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!TORCH_TYPES.contains(block.getType())) {
            return;
        }

        plugin.getTorchManager().getRepository().deleteByLocation(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ()
        );
    }
}
