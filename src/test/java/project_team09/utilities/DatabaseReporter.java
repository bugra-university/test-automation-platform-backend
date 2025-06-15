package project_team09.utilities;

import java.sql.*;
import java.io.File;
import org.testng.*;

import project_team09.database.config.DbConfig;

/**
 * TestNG listener sınıfı - test çalıştırma sonuçlarını veritabanına kaydeder.
 * Test başlangıcı, sonucu, başarı ve başarısızlık durumlarını izler.
 */
public class DatabaseReporter implements ITestListener, ISuiteListener {

    private Connection connection;
    private int currentTestRunId;
    private String gitCommitHash;

    /**
     * Constructor - veritabanı bağlantısı kurar
     */
    public DatabaseReporter() {
        try {
            connection = DbConfig.getConnection();
            gitCommitHash = System.getProperty("git.commit.hash", "unknown");
            System.out.println("DatabaseReporter: Veritabanı bağlantısı kuruldu. Git Commit: " + gitCommitHash);
        } catch (Exception e) {
            System.err.println("DatabaseReporter: Veritabanı bağlantısı kurulamadı!");
            e.printStackTrace();
        }
    }

    /**
     * Test suite başladığında çağrılır
     */
    @Override
    public void onStart(ISuite suite) {
        try {
            // Test çalıştırmasını kaydet
            String sql = "INSERT INTO test_runs (suite_id, name, start_time, status, triggered_by, environment, browser, git_commit_hash) "
                    +
                    "VALUES ((SELECT id FROM test_suites WHERE name = ?), ?, ?, 'RUNNING', ?, ?, ?, ?) RETURNING id";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "TestSuite"); // Sabit değer olarak "TestSuite" kullanıyoruz
            stmt.setString(2, "Run_" + System.currentTimeMillis());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setString(4, System.getProperty("user.name", "MANUAL"));
            stmt.setString(5, System.getProperty("test.environment", "DEVELOPMENT"));
            stmt.setString(6, System.getProperty("test.browser", "CHROME"));
            stmt.setString(7, gitCommitHash);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentTestRunId = rs.getInt(1);
                System.out.println("DatabaseReporter: Test run oluşturuldu, ID: " + currentTestRunId);
            }
            rs.close();
            stmt.close();

            // Git commit bilgisini kaydet
            saveGitCommitInfo();

        } catch (Exception e) {
            System.err.println("DatabaseReporter.onStart: Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test suite bittiğinde çağrılır
     */
    @Override
    public void onFinish(ISuite suite) {
        try {
            // Test çalıştırmasını güncelle
            String sql = "UPDATE test_runs SET end_time = ?, status = 'COMPLETED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, currentTestRunId);

            int updated = stmt.executeUpdate();
            stmt.close();

            System.out.println("DatabaseReporter: Test run tamamlandı, güncellendi: " + updated);

            // Extent Report PDF'ini kaydet
            saveExtentReportPDF();

        } catch (Exception e) {
            System.err.println("DatabaseReporter.onFinish: Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test başladığında çağrılır
     */
    @Override
    public void onTestStart(ITestResult result) {
        try {
            // Test case sonucunu kaydet
            String className = result.getTestClass().getName();
            String methodName = result.getMethod().getMethodName();

            // Önce test case ID'sini bul
            int testCaseId = findTestCaseId(className, methodName);
            if (testCaseId <= 0) {
                System.out.println("DatabaseReporter: Uyarı - Veritabanında test case bulunamadı: " + className + "."
                        + methodName);
                return;
            }

            String sql = "INSERT INTO test_results (test_run_id, test_case_id, status, start_time) " +
                    "VALUES (?, ?, 'RUNNING', ?)";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, currentTestRunId);
            stmt.setInt(2, testCaseId);
            stmt.setTimestamp(3, new Timestamp(result.getStartMillis()));

            stmt.executeUpdate();
            stmt.close();

            System.out.println("DatabaseReporter: Test başladı: " + className + "." + methodName);
        } catch (Exception e) {
            System.err.println("DatabaseReporter.onTestStart: Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test başarılı olduğunda çağrılır
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        updateTestResult(result, "PASSED", null, null);
        System.out.println("DatabaseReporter: Test başarılı: " + result.getTestClass().getName() + "."
                + result.getMethod().getMethodName());
    }

    /**
     * Test başarısız olduğunda çağrılır
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        String stackTrace = null;
        if (result.getThrowable() != null) {
            StackTraceElement[] stackTraceElements = result.getThrowable().getStackTrace();
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : stackTraceElements) {
                sb.append(element.toString()).append("\n");
            }
            stackTrace = sb.toString();
        }

        updateTestResult(result, "FAILED", errorMsg, stackTrace);

        // Hata ekran görüntüsünü kaydet
        saveScreenshot(result);

        System.out.println("DatabaseReporter: Test başarısız: " + result.getTestClass().getName() + "."
                + result.getMethod().getMethodName());
    }

    /**
     * Test atlandığında çağrılır
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        updateTestResult(result, "SKIPPED", null, null);
        System.out.println("DatabaseReporter: Test atlandı: " + result.getTestClass().getName() + "."
                + result.getMethod().getMethodName());
    }

    /**
     * Test sonucunu günceller
     */
    private void updateTestResult(ITestResult result, String status, String errorMsg, String stackTrace) {
        try {
            String className = result.getTestClass().getName();
            String methodName = result.getMethod().getMethodName();

            // Önce test case ID'sini bul
            int testCaseId = findTestCaseId(className, methodName);
            if (testCaseId <= 0) {
                System.out.println("DatabaseReporter: Uyarı - Veritabanında test case bulunamadı: " + className + "."
                        + methodName);
                return;
            }

            String sql = "UPDATE test_results SET status = ?, end_time = ?, duration_ms = ?, error_message = ?, stack_trace = ? "
                    +
                    "WHERE test_run_id = ? AND test_case_id = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(result.getEndMillis()));
            stmt.setLong(3, result.getEndMillis() - result.getStartMillis());
            stmt.setString(4, errorMsg);
            stmt.setString(5, stackTrace);
            stmt.setInt(6, currentTestRunId);
            stmt.setInt(7, testCaseId);

            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            System.err.println("DatabaseReporter.updateTestResult: Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test case ID'sini sınıf ve metod adına göre bulur
     */
    private int findTestCaseId(String className, String methodName) {
        try {
            String sql = "SELECT id FROM test_cases WHERE class_name = ? AND method_name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, className);
            stmt.setString(2, methodName);

            ResultSet rs = stmt.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("id");
            }

            rs.close();
            stmt.close();
            return id;
        } catch (Exception e) {
            System.err.println("Test case ID bulunamadı: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Ekran görüntüsünü veritabanına kaydeder
     */
    private void saveScreenshot(ITestResult result) {
        try {
            // ReusableMethods sınıfından ekran görüntüsü alan metodu kullan
            String screenshotPath = ReusableMethods.getScreenshot(result.getName());

            if (screenshotPath != null) {
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    String className = result.getTestClass().getName();
                    String methodName = result.getMethod().getMethodName();
                    int testCaseId = findTestCaseId(className, methodName);

                    if (testCaseId <= 0) {
                        return;
                    }

                    // Test result ID'sini bul
                    String findSql = "SELECT id FROM test_results WHERE test_run_id = ? AND test_case_id = ?";
                    PreparedStatement findStmt = connection.prepareStatement(findSql);
                    findStmt.setInt(1, currentTestRunId);
                    findStmt.setInt(2, testCaseId);
                    ResultSet rs = findStmt.executeQuery();
                    int testResultId = 0;
                    if (rs.next()) {
                        testResultId = rs.getInt("id");
                    }
                    rs.close();
                    findStmt.close();

                    // Test result ID'sini kullanarak ekran görüntüsünü kaydet
                    String insertSql = "INSERT INTO screenshots (test_result_id, file_name, file_path, content_type, file_size, description) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                    insertStmt.setInt(1, testResultId);
                    insertStmt.setString(2, result.getName() + ".png");
                    insertStmt.setString(3, screenshotPath);
                    insertStmt.setString(4, "image/png");
                    insertStmt.setLong(5, screenshotFile.length());
                    insertStmt.setString(6, result.getName() + " screenshot");
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Ekran görüntüsü kaydedilemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Git commit bilgisini veritabanına kaydeder
     */
    private void saveGitCommitInfo() {
        if (gitCommitHash == null || gitCommitHash.equals("unknown")) {
            return;
        }

        try {
            // Önce bu commit daha önce kaydedilmiş mi kontrol et
            String checkSql = "SELECT id FROM git_commits WHERE commit_hash = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
                stmt.setString(1, gitCommitHash);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Zaten kaydedilmiş
                        return;
                    }
                }
            }

            // Yeni commit bilgisini kaydet
            String insertSql = "INSERT INTO git_commits (commit_hash, author, commit_date, message, branch) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setString(1, gitCommitHash);
                stmt.setString(2, System.getProperty("git.commit.author", "Unknown"));
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.setString(4, System.getProperty("git.commit.message", ""));
                stmt.setString(5, System.getProperty("git.branch", "main"));
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Git commit bilgisi kaydedilemedi: " + e.getMessage());
        }
    }

    /**
     * Extent Report PDF'ini kaydeder
     */
    private void saveExtentReportPDF() {
        // Extent Report PDF'ini kaydetme işlemi burada yapılabilir
        System.out.println("Extent Report PDF'ini kaydetme işlemi burada yapılabilir");
    }
}
