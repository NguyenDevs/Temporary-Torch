package com.NguyenDevs.temporaryTorch.listeners;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TorchBreakListener implements Listener {

    private final TemporaryTorch plugin;

    public TorchBreakListener(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTorchBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() != Material.TORCH && block.getType() != Material.WALL_TORCH) {
            return;
        }

        if (plugin.getConfigManager().isWorldDisabled(block.getWorld().getName())) {
            return;
        }

        if (!plugin.getTorchManager().hasTorch(block.getLocation())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();
        boolean hasSilkTouch = tool != null && tool.containsEnchantment(Enchantment.SILK_TOUCH);

        if (hasSilkTouch) {
            plugin.getTorchManager().removeTorch(block.getLocation());
            return;
        }

        event.setDropItems(false);

        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.getTorchManager().dropItemsManually(block.getLocation());
        }

        plugin.getTorchManager().removeTorch(block.getLocation());
    }
}