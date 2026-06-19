package com.NguyenDevs.temporaryTorch.manager;

import com.NguyenDevs.temporaryTorch.TemporaryTorch;
import com.NguyenDevs.temporaryTorch.database.TorchRepository;
import com.NguyenDevs.temporaryTorch.scheduler.TorchExpiryScheduler;

public class TorchManager {
    private final TemporaryTorch plugin;
    private final TorchRepository repository;
    private final TorchExpiryScheduler scheduler;

    public TorchManager(TemporaryTorch plugin, TorchRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.scheduler = new TorchExpiryScheduler(plugin, repository);
    }

    public TorchRepository getRepository() {
        return repository;
    }

    public void startScheduler() {
        scheduler.start();
    }

    public void stopScheduler() {
        scheduler.cancel();
    }
}
