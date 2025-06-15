package project_team09.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * "Database connection settings and connection pool management.
 */
public class DbConfig {
    private static HikariDataSource dataSource;

    static {
        initDataSource();
    }

    /**
     * Initializes the HikariCP connection pool.
     */
    private static void initDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            // First, get the connection information from system environment variables, if
            // not
            // use defaults
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                // To connect to the postgres service in the Docker environment
                dbUrl = "jdbc:postgresql://postgres:5432/testautomation";
            }

            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isEmpty()) {
                dbUser = "testadmin";
            }

            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null || dbPassword.isEmpty()) {
                dbPassword = "testpassword";
            }

            // Connection pool configuration
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(3);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(600000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            System.out.println("Veritabanı bağlantı havuzu başlatıldı: " + dbUrl);
        } catch (Exception e) {
            System.err.println("Veritabanı bağlantı havuzu başlatılırken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a connection from the connection pool.
     *
     * @return Database connection
     * @throws SQLException If unable to get connection
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool.
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }
}
