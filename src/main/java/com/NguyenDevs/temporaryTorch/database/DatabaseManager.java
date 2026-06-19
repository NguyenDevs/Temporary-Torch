package com.NguyenDevs.temporaryTorch.database;

import javax.sql.DataSource;

public interface DatabaseManager {
    void initialize();
    DataSource getDataSource();
    void shutdown();
}
