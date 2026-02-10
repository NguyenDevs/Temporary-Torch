package com.NguyenDevs.temporaryTorch.models;

import org.bukkit.Location;

public class TorchData {

    private final Location location;
    private final long placedTime;
    private final long duration;
    private final String ownerUUID;

    public TorchData(Location location, long placedTime, long duration, String ownerUUID) {
        this.location = location;
        this.placedTime = placedTime;
        this.duration = duration;
        this.ownerUUID = ownerUUID;
    }

    public Location getLocation() {
        return location;
    }

    public long getPlacedTime() {
        return placedTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public long getRemainingTime() {
        long elapsed = (System.currentTimeMillis() - placedTime) / 1000;
        return Math.max(0, duration - elapsed);
    }

    public boolean isExpired() {
        return getRemainingTime() <= 0;
    }

    public String getLocationKey() {
        return location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }
}