package com.NguyenDevs.temporaryTorch.command;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TorchCommand implements CommandExecutor {
    private final TemporaryTorch plugin;

    public TorchCommand(TemporaryTorch plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        plugin.reloadConfig();
        plugin.getConfigManager().reload();
        sender.sendMessage(plugin.getConfigManager().getMessage("reload-success"));
        return true;
    }
}
