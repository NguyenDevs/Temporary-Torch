package com.NguyenDevs.temporaryTorch.model;

import java.util.UUID;

public record TorchRecord(long id, String world, int x, int y, int z, UUID placedBy, long placedAt, long expireAt) {
}
