package project_team09.tests.us01;


import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.asserts.SoftAssert;
import project_team09.pages.Us01Us02Us03.Anasayfa;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.WebDriverManager;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;
import project_team09.utilities.StepTracker;

public class Us01_KullaniciKaydiYapilabilmeli extends ExtentReport {


    @Test
    public void tc01_KullaniciKayit() {

        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","İstenen tüm bilgiler girildiğinde kayıt işlemi gerçekleşmeli test edildi");

        // Initialize WebDriver with system properties (set by TestExecutionService)
        if (!WebDriverManager.isDriverReady()) {
            System.out.println("[TC01] WebDriverManager not ready, initializing...");
            WebDriverManager.initializeDriver();
        } else {
            System.out.println("[TC01] WebDriverManager already ready");
        }

        // Step tracking will be automatically initialized by EnhancedTestListener
        // Let's execute steps with step tracking

        StepTracker.executeStep("Navigate to AllOverCommerce website", () -> {
            System.out.println("[TC01] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            
            // Take initial screenshot
            WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "test_start");
        });
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        
        StepTracker.executeStep("Verify website is loaded and Register button is visible", () -> {
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });

        StepTracker.executeStep("Click on Register button", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });

        String username = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = ConfigReader.getProperty("signInPassword1");

        StepTracker.executeStep("Enter username: " + username, () -> {
            anasayfa.usernameAs.sendKeys(username);
            extentTest.info("yeni bir username girildi: " + username);
        });

        StepTracker.executeStep("Enter email: " + email, () -> {
            anasayfa.emailAs.sendKeys(email);
            extentTest.info("yeni bir email girildi: " + email);
        });

        StepTracker.executeStep("Enter password", () -> {
            anasayfa.passwordSignUpAs.sendKeys(password);
            extentTest.info("şifre girildi");
        });

        StepTracker.executeStep("Agree to privacy policy", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });

        StepTracker.executeStep("Click Sign Up button", () -> {
            anasayfa.signUpYeniKayit.click();
            extentTest.info("Sign Up butonuna tıklandı");
            ReusableMethods.bekle(2);
        });


        StepTracker.executeStep("Verify successful registration", () -> {
            System.out.println("[TC01] Checking for signOut element...");
            
            try {
                ReusableMethods.bekle(5); // Wait for page to load
                boolean isSignOutVisible = anasayfa.signOutAs.isDisplayed();
                System.out.println("[TC01] SignOut element visible: " + isSignOutVisible);
                softAssert.assertTrue(isSignOutVisible);
                extentTest.info("Kayıt işleminin gerçekleştiğini doğrulandı");
                
                // Take final screenshot
                WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "registration_success");
            } catch (Exception e) {
                System.out.println("[TC01] ERROR: Could not find signOut element: " + e.getMessage());
                extentTest.info("ERROR: SignOut element not found - registration may have failed");
                // Take screenshot for debugging
                WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "registration_failed");
                softAssert.fail("Registration verification failed: " + e.getMessage());
                throw e; // Re-throw to mark step as failed
            }
        });

        //sayfayı kapat - WebDriverManager handles cleanup via listeners
        // Driver.closeDriver(); // Removed - let WebDriverManager handle this
        extentTest.info("test completed successfully");
        
        // Assert all soft assertions
        softAssert.assertAll();


    }


    @Test
    public void tc02_withoutUsernameNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01)
        System.out.println("[TC02] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC02] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC02] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusunu boş bırak
        extentTest.info("username kutusu boş bırakıldı");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();


        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");

    }

    @Test
    public void tc03_withoutEmailNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","email alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01, TC02)
        System.out.println("[TC03] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC03] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC03] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");


        //email kutusunu boş bırak
        extentTest.info("email alanı boş bırakıldı");


        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        ReusableMethods.bekle(2);
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(5);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");


    }

    @Test
    public void tc04_withoutPasswordNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","password alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edilidi");

        // Initialize WebDriver with WebDriverManager (like TC01, TC02, TC03)
        System.out.println("[TC04] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC04] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC04] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusunu boş bırak
        extentTest.info("şifre kutusu boş bırakıldı");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc05_WithoutIagreeClickNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı", "I agree to the privacy policy\" kontrol kutusuna tıklanmadığında SignUp işlemi gerçekleşmemeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC04)
        System.out.println("[TC05] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC05] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC05] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıklama
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklanmadı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc06_hataliemailileKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı", "Email alanına @ sembolü eklenmeden girilen mail ile kayıt işlemi gerçekleşmemeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC05)
        System.out.println("[TC06] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC06] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC06] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //@ işareti olmadan email gir
        anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliEmail1"));
        extentTest.info("@ işareti olmadan email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc07_withoutComEmailIleKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("kullanıcı kaydı","Email alanına .com eklenmeden kayıt işlemi gerçekleşmemeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC06)
        System.out.println("[TC07] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC07] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC07] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //.com  olmadan email gir
        anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliemail2"));
        extentTest.info(".com  olmadan email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc08_sekizChrctrPasswordkayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 8 karakterli bir şifre girilerek kayıt olunabilmeli");

        // Initialize WebDriver with WebDriverManager (like TC01-TC07)
        System.out.println("[TC08] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC08] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC08] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 8 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password8");
        extentTest.info(" 8 karakterli şifre girildi");


        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula
        ReusableMethods.bekle(5); // Wait for page to load
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }

    @Test
    public void tc09_dokuzChrctrPasswordkayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 9 karakterli bir şifre girilerek kayıt olunabilmeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC08)
        System.out.println("[TC09] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC09] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC09] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");


        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 9 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password9");
        extentTest.info("9 karakterli bir şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula
        ReusableMethods.bekle(5); // Wait for page to load
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc10_yediChrctrPasswordkayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 7 karakterli bir şifre girilerek kayıt olunamamalı test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC09)
        System.out.println("[TC10] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC10] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC10] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 7 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password7");
        extentTest.info("7 karakterli bir şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");


        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        ReusableMethods.tumSayfaResmi("us01tc10kayit");
        extentTest.info("hatalı bilgiler ile kayıt yapıldı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc11_passwordSadeceRakamlaKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına sadece rakamlardan oluşan bir şifre girilerek kayıt olunamamalı test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC10)
        System.out.println("[TC11] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC11] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC11] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna sadece rakamlardan şifre gir
        anasayfa.passwordSignUpAs.sendKeys("passwordRakam");
        extentTest.info("sadece rakamlardan oluşan şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Kayıt işleminin gerçekleşmediği doğrulandı");
        ReusableMethods.tumSayfaResmi("us01tc11kayit");
        extentTest.info("hatalı bilgilerle kayıt yapıldı");
        softAssert.assertAll();

        //Sayfayı kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }

    @Test
    public void tc12_buyukkucukHarfRakamUsernameKayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanına büyük harf, küçük harf ve rakam girilerek kayıt olunabilmeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC11)
        System.out.println("[TC12] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC12] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC12] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna büyük harf, küçük harf ve rakam içeren kullanıcı adı gir

        anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername2"));
        extentTest.info("username kutusuna büyük harf, küçük harf ve rakam içeren kullanıcı adı girildi");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        ReusableMethods.bekle(10);

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        extentTest.info("Sign Up butonuna tıklandı");


        //Kayıt işleminin gerçekleştiğini doğrula
        ReusableMethods.bekle(5); // Wait for page to load
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");
        softAssert.assertAll();

        //Sayfayi kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }


    @Test
    public void tc13_usernameOzelkarakterKayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanına özel karakter girilerek kayıt olunabilmeli test edildi");

        // Initialize WebDriver with WebDriverManager (like TC01-TC12)
        System.out.println("[TC13] Initializing WebDriverManager...");
        WebDriverManager.initializeDriver();
        System.out.println("[TC13] WebDriverManager ready: " + WebDriverManager.isDriverReady());

        //Web sitesine git ve doğrula
        System.out.println("[TC13] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
        WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        
        // Initialize page object AFTER WebDriverManager is ready
        Anasayfa anasayfa = new Anasayfa();
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna  özel karakter(*) içeren kullanıcı adı gir

        anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername3"));
        extentTest.info("username kutusuna  özel karakter(*) içeren kullanıcı adı girildi");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula
        ReusableMethods.bekle(5); // Wait for page to load
        ReusableMethods.tumSayfaResmi("us01tc13kayit");
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Kayıt işleminin gerçekleştiğini doğrulanmak istendi fakat hata bulundu");
        softAssert.assertAll();

        //Sayfayi kapat - WebDriverManager handles cleanup via @AfterMethod
        // Driver.closeDriver(); // Removed - let @AfterMethod handle this
        extentTest.info("test completed successfully");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("[AfterMethod] Cleaning up browser...");
        
        // Only clean up WebDriverManager (since all tests now use it)
        if (WebDriverManager.isDriverReady()) {
            System.out.println("[AfterMethod] Closing browser...");
            WebDriverManager.quitDriver();
            System.out.println("[AfterMethod] Browser closed successfully");
        } else {
            System.out.println("[AfterMethod] No browser to close");
        }
    }
}
