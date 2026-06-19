package com.NguyenDevs.temporaryTorch.listener;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.model.TorchRecord;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TorchRefuelListener implements Listener {
    private static final Set<Material> TORCH_TYPES = Set.of(
            Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH,
            Material.WALL_TORCH, Material.SOUL_WALL_TORCH, Material.REDSTONE_WALL_TORCH
    );

    private final TemporaryTorch plugin;

    public TorchRefuelListener(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTorchRefuel(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!TORCH_TYPES.contains(block.getType())) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        long addedSeconds;
        if (item.getType() == Material.COAL) {
            addedSeconds = plugin.getConfigManager().getRefuelCoalSeconds();
        } else if (item.getType() == Material.CHARCOAL) {
            addedSeconds = plugin.getConfigManager().getRefuelCharcoalSeconds();
        } else {
            return;
        }

        if (addedSeconds <= 0) {
            return;
        }

        if (!player.hasPermission("temporarytorch.refuel")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }

        Optional<TorchRecord> optRecord = plugin.getTorchManager().getRepository().findByLocation(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ()
        );

        if (optRecord.isEmpty()) {
            return;
        }

        TorchRecord record = optRecord.get();
        UUID playerUuid = player.getUniqueId();

        if (!plugin.getConfigManager().isAllowOthersRefuel() && !record.placedBy().equals(playerUuid)) {
            player.sendMessage(plugin.getConfigManager().getMessage("torch-refuel-denied"));
            return;
        }

        if (!record.placedBy().equals(playerUuid) && !player.hasPermission("temporarytorch.refuel")) {
            player.sendMessage(plugin.getConfigManager().getMessage("torch-refuel-denied"));
            return;
        }

        long newExpireAt = record.expireAt() + (addedSeconds * 1000);
        long maxLifetime = plugin.getConfigManager().getMaxTorchLifetimeSeconds();

        if (maxLifetime >= 0) {
            long maxExpireAt = record.placedAt() + (maxLifetime * 1000);
            if (newExpireAt > maxExpireAt) {
                newExpireAt = maxExpireAt;
                if (newExpireAt <= record.expireAt()) {
                    player.sendMessage(plugin.getConfigManager().getMessage("torch-refuel-cap"));
                    return;
                }
            }
        }

        plugin.getTorchManager().getRepository().updateExpireAt(record.id(), newExpireAt);

        item.setAmount(item.getAmount() - 1);

        long remainingSeconds = (newExpireAt - System.currentTimeMillis()) / 1000;
        if (remainingSeconds < 0) {
            remainingSeconds = 0;
        }

        player.sendMessage(plugin.getConfigManager().getMessage("torch-refueled")
                .replace("{added}", String.valueOf(addedSeconds))
                .replace("{remaining}", String.valueOf(remainingSeconds)));
    }
}
