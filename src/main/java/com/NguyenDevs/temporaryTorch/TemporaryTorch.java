package com.NguyenDevs.temporaryTorch;

import com.NguyenDevs.temporaryTorch.commands.ReloadCommand;
import com.NguyenDevs.temporaryTorch.manager.ConfigManager;
import com.NguyenDevs.temporaryTorch.manager.MessageManager;
import com.NguyenDevs.temporaryTorch.listeners.TorchPlaceListener;
import com.NguyenDevs.temporaryTorch.listeners.TorchBreakListener;
import com.NguyenDevs.temporaryTorch.manager.TorchManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class TemporaryTorch extends JavaPlugin {

    private static TemporaryTorch instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private TorchManager torchManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.torchManager = new TorchManager(this);

        // Load configurations
        configManager.loadConfig();
        messageManager.loadMessages();
        torchManager.loadData();

        // Register listeners
        getServer().getPluginManager().registerEvents(new TorchPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new TorchBreakListener(this), this);

        // Register commands
        getCommand("temporarytorch").setExecutor(new ReloadCommand(this));

        // Start torch decay task
        torchManager.startDecayTask();

        printLogo();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&6TemporaryTorch&e] &aTemporaryTorch plugin enabled successfully!"));
    }

    @Override
    public void onDisable() {
        if (torchManager != null) {
            torchManager.saveData();
            torchManager.stopDecayTask();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&6TemporaryTorch&e] &cTemporaryTorch plugin disabled!"));
    }

    public static TemporaryTorch getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public TorchManager getTorchManager() {
        return torchManager;
    }

    public void printLogo() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6   ████████╗███████╗███╗   ███╗██████╗  ██████╗ ██████╗  █████╗ ██████╗ ██╗   ██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6   ╚══██╔══╝██╔════╝████╗ ████║██╔══██╗██╔═══██╗██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6      ██║   █████╗  ██╔████╔██║██████╔╝██║   ██║██████╔╝███████║██████╔╝ ╚████╔╝ "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6      ██║   ██╔══╝  ██║╚██╔╝██║██╔═══╝ ██║   ██║██╔══██╗██╔══██║██╔══██╗  ╚██╔╝  "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6      ██║   ███████╗██║ ╚═╝ ██║██║     ╚██████╔╝██║  ██║██║  ██║██║  ██║   ██║   "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6      ╚═╝   ╚══════╝╚═╝     ╚═╝╚═╝      ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e   ████████╗ ██████╗ ██████╗  ██████╗██╗  ██╗"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e   ╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝██║  ██║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e      ██║   ██║   ██║██████╔╝██║     ███████║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e      ██║   ██║   ██║██╔══██╗██║     ██╔══██║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e      ██║   ╚██████╔╝██║  ██║╚██████╗██║  ██║"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e      ╚═╝    ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e         Temporary Torch"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6         Version " + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b         Development by NguyenDevs"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
    }
}