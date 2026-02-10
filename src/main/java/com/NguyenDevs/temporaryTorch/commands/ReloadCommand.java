package com.NguyenDevs.temporaryTorch.commands;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final TemporaryTorch plugin;

    public ReloadCommand(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("prefix") + " " + plugin.getMessageManager().getMessage("commands.usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!PermissionUtils.hasAdminPermission(player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("prefix") + " " + plugin.getMessageManager().getMessage("commands.no-permission"));
                    return true;
                }
            }

            plugin.getTorchManager().saveData();
            plugin.getConfigManager().reloadConfig();
            plugin.getMessageManager().reloadMessages();
            plugin.getTorchManager().loadData();

            plugin.getTorchManager().stopDecayTask();
            plugin.getTorchManager().startDecayTask();

            sender.sendMessage(plugin.getMessageManager().getMessage("prefix") + " " +  plugin.getMessageManager().getMessage("commands.reload-success"));
            return true;
        }

        sender.sendMessage(plugin.getMessageManager().getMessage("prefix") + " " + plugin.getMessageManager().getMessage("commands.usage"));
        return true;
    }
}