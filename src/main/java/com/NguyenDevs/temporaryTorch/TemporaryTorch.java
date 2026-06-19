package com.NguyenDevs.temporaryTorch;

import com.NguyenDevs.temporaryTorch.command.TorchCommand;
import com.NguyenDevs.temporaryTorch.config.ConfigManager;
import com.NguyenDevs.temporaryTorch.database.DatabaseManager;
import com.NguyenDevs.temporaryTorch.database.H2DatabaseManager;
import com.NguyenDevs.temporaryTorch.database.TorchRepository;
import com.NguyenDevs.temporaryTorch.database.TorchRepositoryImpl;
import com.NguyenDevs.temporaryTorch.listener.TorchBreakListener;
import com.NguyenDevs.temporaryTorch.listener.TorchPlaceListener;
import com.NguyenDevs.temporaryTorch.listener.TorchRefuelListener;
import com.NguyenDevs.temporaryTorch.manager.TorchManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class TemporaryTorch extends JavaPlugin {
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private TorchRepository torchRepository;
    private TorchManager torchManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        configManager.load();

        this.databaseManager = new H2DatabaseManager(this);
        databaseManager.initialize();

        this.torchRepository = new TorchRepositoryImpl(databaseManager.getDataSource());
        this.torchManager = new TorchManager(this, torchRepository);

        getServer().getPluginManager().registerEvents(new TorchPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new TorchBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new TorchRefuelListener(this), this);

        PluginCommand ttCommand = getCommand("tt");
        if (ttCommand != null) {
            ttCommand.setExecutor(new TorchCommand(this));
        }

        torchManager.startScheduler();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (torchManager != null) {
            torchManager.stopScheduler();
        }

        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TorchManager getTorchManager() {
        return torchManager;
    }
}
