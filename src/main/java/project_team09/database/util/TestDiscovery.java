package project_team09.database.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import project_team09.database.config.DbConfig;

/**
 * To automatically discover test classes and save them to the database.
 * This is a utility class.
 */
public class TestDiscovery {

    /**
     * Discovers all TestNG test methods in the project and saves them to the
     * database.
     */
    public static void discoverAndSaveTests() {
        try (Connection conn = DbConfig.getConnection()) {
            System.out.println("Test keşif işlemi başlatılıyor...");

            // Reflections kütüphanesi ile test paketini tara
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackage("project_team09.tests")
                    .setScanners(Scanners.MethodsAnnotated,
                            Scanners.TypesAnnotated,
                            Scanners.SubTypes));

            // Find methods annotated with @Test.
            Set<Method> testMethods = reflections.getMethodsAnnotatedWith(Test.class);
            System.out.println(testMethods.size() + " test metodu bulundu.");

            for (Method method : testMethods) {
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();
                String packageName = method.getDeclaringClass().getPackage().getName();

                // Determine which User Story the test belongs to.
                String userStory = "Unknown";
                if (packageName.contains("us")) {
                    String[] parts = packageName.split("\\.");
                    for (String part : parts) {
                        if (part.startsWith("us")) {
                            userStory = part.toUpperCase();
                            break;
                        }
                    }
                }

                // Get the test description from the TestNG annotation
                String description = "";
                Test testAnnotation = method.getAnnotation(Test.class);
                if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
                    description = testAnnotation.description();
                } else {
                    // If no description, create a meaningful one from the method name
                    description = formatMethodName(methodName);
                }

                // Check or create the test suite in the database
                int suiteId = getSuiteIdOrCreate(conn, userStory, packageName);

                // Check or create the test case in the database
                saveTestCase(conn, suiteId, formatMethodName(methodName), description, className, methodName);

                System.out.println("Test kaydedildi: " + className + "." + methodName);
            }

            System.out.println("Test keşif işlemi tamamlandı.");
        } catch (Exception e) {
            System.err.println("Test keşif işlemi sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Makes the method name more readable.
     * "Example: 'tc01_kayitliBilgiKaydiYapilmamali' -> 'TC01 Kayıtlı Bilgi Kaydı
     * Yapılmamalı'."
     */
    private static String formatMethodName(String methodName) {
        String formatted = methodName;

        // Convert prefixes like 'tc01_' to uppercase.
        if (formatted.matches("tc\\d+.*")) {
            int digitEnd = 0;
            while (digitEnd < formatted.length() &&
                    (formatted.charAt(digitEnd) == 't' ||
                            formatted.charAt(digitEnd) == 'c' ||
                            Character.isDigit(formatted.charAt(digitEnd)) ||
                            formatted.charAt(digitEnd) == '_')) {
                digitEnd++;
            }

            if (digitEnd > 0) {
                String prefix = formatted.substring(0, digitEnd).toUpperCase().replace("_", " ");
                formatted = prefix + (digitEnd < formatted.length() ? formatted.substring(digitEnd) : "");
            }
        }

        // Separate CamelCase with spaces
        formatted = formatted.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Capitalize the first letter
        if (!formatted.isEmpty()) {
            formatted = Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
        }

        return formatted;
    }

    /**
     * Searches for a test suite in the database, creates one if it doesn't exist.
     *
     * @return Suite ID
     */
    private static int getSuiteIdOrCreate(Connection conn, String userStory, String packageName) throws Exception {
        String sql = "SELECT id FROM test_suites WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userStory);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        // Create a new suite
        String sql2 = "INSERT INTO test_suites (project_id, name, description, folder_path) VALUES " +
                "((SELECT id FROM projects WHERE name='Project Team09'), ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.setString(1, userStory);
            stmt.setString(2, userStory + " Tests");
            stmt.setString(3, packageName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new Exception("Test suite could not be created: " + userStory);
    }

    /**
     * Saves a test case to the database, updates it if it already exists.
     */
    private static void saveTestCase(Connection conn, int suiteId, String name, String description,
            String className, String methodName) throws Exception {
        // First, check if this test case exists
        String sql = "SELECT id FROM test_cases WHERE class_name = ? AND method_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            stmt.setString(2, methodName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Test case already exists, update it
                    int id = rs.getInt("id");
                    String updateSql = "UPDATE test_cases SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, name);
                        updateStmt.setString(2, description);
                        updateStmt.setInt(3, id);
                        updateStmt.executeUpdate();
                    }
                    return;
                }
            }
        }

        // Create a new test case
        String insertSql = "INSERT INTO test_cases (suite_id, name, description, class_name, method_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, suiteId);
            insertStmt.setString(2, name);
            insertStmt.setString(3, description);
            insertStmt.setString(4, className);
            insertStmt.setString(5, methodName);
            insertStmt.executeUpdate();
        }
    }

    /**
     * Main method - runs when the program starts
     */
    public static void main(String[] args) {
        discoverAndSaveTests();
    }
}
