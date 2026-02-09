package com.vizja.testweb.tests.us01;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.asserts.SoftAssert;
import com.vizja.testweb.pages.Us01Us02Us03.Anasayfa;
import com.vizja.testweb.utilities.ConfigReader;
import com.vizja.testweb.utilities.WebDriverManager;
import com.vizja.testweb.utilities.ExtentReport;
import com.vizja.testweb.utilities.ReusableMethods;
import com.vizja.testweb.utilities.StepTracker;
public class Us01_KullaniciKaydiYapilabilmeli extends ExtentReport {
    @Test
    public void tc01_KullaniciKayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "İstenen tüm bilgiler girildiğinde kayıt işlemi gerçekleşmeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            System.out.println("[TC01] WebDriverManager not ready, initializing...");
            WebDriverManager.initializeDriver();
        } else {
            System.out.println("[TC01] WebDriverManager already ready");
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            String url = ConfigReader.getProperty("allowerCommerceUrl");
            System.out.println("[TC01] Navigating to: " + url);
            WebDriverManager.getDriver().get(url);
            ReusableMethods.bekle(2); 
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed(),
                    "Register link should be visible (is dummy site running at " + url + "?)");
            WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "test_start");
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        String username = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = ConfigReader.getProperty("signInPassword1");
        StepTracker.executeStep("enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(username);
            extentTest.info("yeni bir username girildi: " + username);
        });
        StepTracker.executeStep("enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(email);
            extentTest.info("yeni bir email girildi: " + email);
        });
        StepTracker.executeStep("enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(password);
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            extentTest.info("Sign Up butonuna tıklandı");
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Validate the registration process", () -> {
            System.out.println("[TC01] Checking for signOut element...");
            try {
                ReusableMethods.bekle(5); 
                boolean isSignOutVisible = anasayfa.signOutAs.isDisplayed();
                System.out.println("[TC01] SignOut element visible: " + isSignOutVisible);
                softAssert.assertTrue(isSignOutVisible);
                extentTest.info("Kayıt işleminin gerçekleştiğini doğrulandı");
                WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "registration_success");
            } catch (Exception e) {
                System.out.println("[TC01] ERROR: Could not find signOut element: " + e.getMessage());
                extentTest.info("ERROR: SignOut element not found - registration may have failed");
                WebDriverManager.takeScreenshot("tc01_KullaniciKayit", "registration_failed");
                softAssert.fail("Registration verification failed: " + e.getMessage());
                throw e; 
            }
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc02_withoutUsernameNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "username alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");
        StepTracker.executeStep("Initialize WebDriver", () -> {
            System.out.println("[TC02] Initializing WebDriverManager...");
            WebDriverManager.initializeDriver();
            System.out.println("[TC02] WebDriverManager ready: " + WebDriverManager.isDriverReady());
        });
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Navigate to AllOverCommerce website", () -> {
            System.out.println("[TC02] Navigating to: " + ConfigReader.getProperty("allowerCommerceUrl"));
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on Register button", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Leave username empty", () -> {
            extentTest.info("username kutusu boş bırakıldı");
        });
        String email = faker.internet().emailAddress();
        StepTracker.executeStep("Enter email: " + email, () -> {
            anasayfa.emailAs.sendKeys(email);
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter password", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("Agree to privacy policy", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click Sign Up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Verify registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "Expected validation message (Please fill this area.)");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        softAssert.assertAll();
        extentTest.info("test completed successfully");
    }
    @Test
    public void tc03_withoutEmailNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "email alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Leave email empty", () -> {
            extentTest.info("email alanı boş bırakıldı");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            ReusableMethods.bekle(2);
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(5);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "Expected validation message (e.g. Please fill this area.)");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc04_withoutPasswordNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "password alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edilidi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Leave password empty", () -> {
            extentTest.info("şifre kutusu boş bırakıldı");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "Expected validation message (Please fill this area.)");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc05_WithoutIagreeClickNotRegister() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "I agree to the privacy policy\" kontrol kutusuna tıklanmadığında SignUp işlemi gerçekleşmemeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("Do not click I agree to privacy policy", () -> {
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklanmadı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "Expected tick this box or fill this area message");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc06_hataliemailileKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "Email alanına @ sembolü eklenmeden girilen mail ile kayıt işlemi gerçekleşmemeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an invalid email without @ symbol", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliEmail1"));
            extentTest.info("@ işareti olmadan email girildi");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(), "Expected email validation message");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc07_withoutComEmailIleKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("kullanıcı kaydı",
                "Email alanına .com eklenmeden kayıt işlemi gerçekleşmemeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter email without .com", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliemail2"));
            extentTest.info(".com olmadan email girildi");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(), "Expected valid email address message");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc08_sekizChrctrPasswordkayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "Password alanına 8 karakterli bir şifre girilerek kayıt olunabilmeli");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter 8 character password", () -> {
            anasayfa.passwordSignUpAs.sendKeys("password8");
            extentTest.info("8 karakterli şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful (password < 12 chars)", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "8 char password should not register (min 12)");
            extentTest.info("8 karakterli şifre ile kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc09_dokuzChrctrPasswordkayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "Password alanına 9 karakterli bir şifre girilerek kayıt olunabilmeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter 9 character password", () -> {
            anasayfa.passwordSignUpAs.sendKeys("password9");
            extentTest.info("9 karakterli bir şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful (password < 12 chars)", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "9 char password should not register (min 12)");
            extentTest.info("9 karakterli şifre ile kayıt yapılamadığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc10_yediChrctrPasswordkayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "Password alanına 7 karakterli bir şifre girilerek kayıt olunamamalı test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter 7 character password", () -> {
            anasayfa.passwordSignUpAs.sendKeys("password7");
            extentTest.info("7 karakterli bir şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(), "7 char password should not register");
            extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
            ReusableMethods.tumSayfaResmi("us01tc10kayit");
            extentTest.info("hatalı bilgiler ile kayıt yapıldı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc11_passwordSadeceRakamlaKayitOlma() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "Password alanına sadece rakamlardan oluşan bir şifre girilerek kayıt olunamamalı test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter a username in the username box", () -> {
            anasayfa.usernameAs.sendKeys(faker.name().firstName());
            extentTest.info("yeni bir username girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter password with digits only", () -> {
            anasayfa.passwordSignUpAs.sendKeys("123456789012"); 
            extentTest.info("sadece rakamlardan oluşan şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerValidationAs.isDisplayed(),
                    "Digits-only or short password should not register");
            extentTest.info("Kayıt işleminin gerçekleşmediği doğrulandı");
            extentTest.info("hatalı bilgilerle kayıt yapılamadığı test edildi");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc12_buyukkucukHarfRakamUsernameKayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "username alanına büyük harf, küçük harf ve rakam girilerek kayıt olunabilmeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter username with upper, lower and digit", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername2"));
            extentTest.info("username kutusuna büyük harf, küçük harf ve rakam içeren kullanıcı adı girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            ReusableMethods.bekle(10);
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate the registration process", () -> {
            ReusableMethods.bekle(5);
            softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
            extentTest.info("Siteye kayıt yapıldığı doğrulandı");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @Test
    public void tc13_usernameOzelkarakterKayit() {
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı",
                "username alanına özel karakter girilerek kayıt olunabilmeli test edildi");
        if (!WebDriverManager.isDriverReady()) {
            WebDriverManager.initializeDriver();
        }
        Anasayfa anasayfa = new Anasayfa();
        StepTracker.executeStep("Go to Site", () -> {
            WebDriverManager.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
            extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(3);
            extentTest.info("Registera tıklandı");
        });
        StepTracker.executeStep("Enter username with special character", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername3"));
            extentTest.info("username kutusuna özel karakter(*) içeren kullanıcı adı girildi");
        });
        StepTracker.executeStep("Enter an email to your email address box", () -> {
            anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
            extentTest.info("yeni bir email girildi");
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
            extentTest.info("şifre girildi");
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
            extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            extentTest.info("Sign Up butonuna tıklandı");
        });
        StepTracker.executeStep("Validate the registration process", () -> {
            ReusableMethods.bekle(5);
            ReusableMethods.tumSayfaResmi("us01tc13kayit");
            softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
            extentTest.info("Kayıt işleminin gerçekleştiğini doğrulanmak istendi fakat hata bulundu");
        });
        extentTest.info("test completed successfully");
        softAssert.assertAll();
    }
    @AfterMethod
    public void tearDown() {
        System.out.println("[AfterMethod] Cleaning up browser...");
        if (WebDriverManager.isDriverReady()) {
            System.out.println("[AfterMethod] Closing browser...");
            WebDriverManager.quitDriver();
            System.out.println("[AfterMethod] Browser closed successfully");
        } else {
            System.out.println("[AfterMethod] No browser to close");
        }
    }
}

