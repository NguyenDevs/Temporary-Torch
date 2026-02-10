package com.NguyenDevs.temporaryTorch.listeners;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.utils.PermissionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TorchPlaceListener implements Listener {

    private final TemporaryTorch plugin;

    public TorchPlaceListener(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTorchPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

        if (block.getType() != Material.TORCH && block.getType() != Material.WALL_TORCH) {
            return;
        }

        if (plugin.getConfigManager().isWorldDisabled(block.getWorld().getName())) {
            return;
        }

        if (PermissionUtils.hasBypassPermission(player)) {
            return;
        }

        plugin.getTorchManager().addTorch(block.getLocation(), player);
    }
}