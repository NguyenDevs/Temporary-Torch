package com.NguyenDevs.temporaryTorch.database;

import com.NguyenDevs.temporaryTorch.model.TorchRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TorchRepository {
    void insert(TorchRecord record);
    void deleteByLocation(String world, int x, int y, int z);
    void deleteById(long id);
    Optional<TorchRecord> findByLocation(String world, int x, int y, int z);
    List<TorchRecord> findExpired(long currentTime);
    long countByPlacedBy(UUID placedBy);
    void updateExpireAt(long id, long newExpireAt);
}
