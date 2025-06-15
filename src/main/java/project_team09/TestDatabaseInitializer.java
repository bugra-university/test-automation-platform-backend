package project_team09;

import project_team09.database.config.DbConfig;
import project_team09.database.util.TestDiscovery;

/**
 * Class that initializes the test database and discovers test classes.
 */
public class TestDatabaseInitializer {

    /**
     * Main method
     */
    public static void main(String[] args) {
        try {
            System.out.println("Test database is starting...");

            // Discover test classes and save them to the database
            TestDiscovery.discoverAndSaveTests();

            System.out.println("Test database initialization completed.");
        } catch (Exception e) {
            System.err.println("Error occurred while initializing test database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the database connection pool
            DbConfig.closeDataSource();
        }
    }
}
