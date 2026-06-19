package com.NguyenDevs.temporaryTorch.database;

import com.NguyenDevs.temporaryTorch.model.TorchRecord;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TorchRepositoryImpl implements TorchRepository {
    private static final String INSERT = "INSERT INTO torches (world, x, y, z, placed_by, placed_at, expire_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BY_LOCATION = "DELETE FROM torches WHERE world = ? AND x = ? AND y = ? AND z = ?";
    private static final String DELETE_BY_ID = "DELETE FROM torches WHERE id = ?";
    private static final String SELECT_BY_LOCATION = "SELECT * FROM torches WHERE world = ? AND x = ? AND y = ? AND z = ?";
    private static final String SELECT_EXPIRED = "SELECT * FROM torches WHERE expire_at <= ?";
    private static final String COUNT_BY_PLACED_BY = "SELECT COUNT(*) FROM torches WHERE placed_by = ?";
    private static final String UPDATE_EXPIRE_AT = "UPDATE torches SET expire_at = ? WHERE id = ?";

    private final DataSource dataSource;

    public TorchRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(TorchRecord record) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            stmt.setString(1, record.world());
            stmt.setInt(2, record.x());
            stmt.setInt(3, record.y());
            stmt.setInt(4, record.z());
            stmt.setString(5, record.placedBy().toString());
            stmt.setLong(6, record.placedAt());
            stmt.setLong(7, record.expireAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert torch record", e);
        }
    }

    @Override
    public void deleteByLocation(String world, int x, int y, int z) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_BY_LOCATION)) {
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete torch by location", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_BY_ID)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete torch by id", e);
        }
    }

    @Override
    public Optional<TorchRecord> findByLocation(String world, int x, int y, int z) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LOCATION)) {
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find torch by location", e);
        }
        return Optional.empty();
    }

    @Override
    public List<TorchRecord> findExpired(long currentTime) {
        List<TorchRecord> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_EXPIRED)) {
            stmt.setLong(1, currentTime);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expired torches", e);
        }
        return results;
    }

    @Override
    public long countByPlacedBy(UUID placedBy) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(COUNT_BY_PLACED_BY)) {
            stmt.setString(1, placedBy.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count torches by player", e);
        }
        return 0;
    }

    @Override
    public void updateExpireAt(long id, long newExpireAt) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPIRE_AT)) {
            stmt.setLong(1, newExpireAt);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expire_at", e);
        }
    }

    private TorchRecord mapRow(ResultSet rs) throws SQLException {
        return new TorchRecord(
                rs.getLong("id"),
                rs.getString("world"),
                rs.getInt("x"),
                rs.getInt("y"),
                rs.getInt("z"),
                UUID.fromString(rs.getString("placed_by")),
                rs.getLong("placed_at"),
                rs.getLong("expire_at")
        );
    }
}
