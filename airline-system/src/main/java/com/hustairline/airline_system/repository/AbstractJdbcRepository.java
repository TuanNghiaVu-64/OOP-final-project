package com.hustairline.airline_system.repository;

import com.hustairline.airline_system.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abstract JDBC repository that centralizes connection handling.
 * All concrete repositories should extend this class to avoid
 * duplicating DB_URL, USER, and PASS constants.
 */
public abstract class AbstractJdbcRepository {

    private final DatabaseConfig dbConfig;

    @Autowired
    public AbstractJdbcRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * Obtain a new JDBC connection using the injected DatabaseConfig.
     * Concrete repositories should use try-with-resources like:
     * <pre>
     * try (Connection conn = getConnection();
     *      PreparedStatement stmt = conn.prepareStatement(sql)) {
     *      ...
     * }
     * </pre>
     * @return a new {@link Connection}
     * @throws SQLException if connection cannot be established
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                dbConfig.getUrl(),
                dbConfig.getUsername(),
                dbConfig.getPassword()
        );
    }
} 