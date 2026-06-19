package com.NguyenDevs.temporaryTorch.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DatabaseManager implements DatabaseManager {
    private static final String DB_FILENAME = "torches";
    private static final String TABLE_TORCHES = """
            CREATE TABLE IF NOT EXISTS torches (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                world VARCHAR(64) NOT NULL,
                x INT NOT NULL,
                y INT NOT NULL,
                z INT NOT NULL,
                placed_by VARCHAR(36) NOT NULL,
                placed_at BIGINT NOT NULL,
                expire_at BIGINT NOT NULL
            )
            """;
    private static final String UNIQUE_INDEX = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_torches_location ON torches (world, x, y, z)
            """;

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public H2DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        File dbFolder = plugin.getDataFolder();
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        String dbPath = new File(dbFolder, DB_FILENAME).getAbsolutePath();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:" + dbPath + ";DB_CLOSE_DELAY=-1;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setPoolName("TemporaryTorch-H2");
        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(TABLE_TORCHES);
            stmt.execute(UNIQUE_INDEX);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create database tables: " + e.getMessage());
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
