package com.vizja.testweb.tests.us02;

import com.github.javafaker.Faker;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.vizja.testweb.pages.Us01Us02Us03.Anasayfa;
import org.openqa.selenium.WebDriver;
import com.vizja.testweb.utilities.ConfigReader;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.ExtentReport;
import com.vizja.testweb.utilities.ReusableMethods;
import com.vizja.testweb.utilities.StepTracker;
import com.vizja.testweb.utilities.WebDriverManager;

public class Us02_GecersizYeniKullaniciKayit extends ExtentReport {
    private static WebDriver driver() {
        return WebDriverManager.isDriverReady() ? WebDriverManager.getDriver() : Driver.getDriver();
    }

    private static String siteUrl() {
        String url = ConfigReader.getProperty("allowerCommerceUrl");
        if (url == null || url.trim().isEmpty())
            throw new IllegalStateException(
                    "allowerCommerceUrl is not set in configuration.properties (current dir or backend/)");
        return url.trim();
    }

    @Test
    public void tc01_kayitliBilgiKaydiYapilmamali() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC01 Kayıtlı Bilgi Kaydı Yapılmamalı Testi",
                "Kayıtlı bilgilerle yeni kayıt yapılamadığı doğrulanır");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(5);
        });
        StepTracker.executeStep("Enter registered username", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("username1as"));
        });
        StepTracker.executeStep("Enter registered email", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("signInEmail1"));
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
        });
        StepTracker.executeStep("Verify registered data error is displayed", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.kayitliBilgiHataAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc02_kayitliUsernameSignUpOlma() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC02 Kayıtlı Username ile Kayıt Testi",
                "Kayıtlı username ile yeni kayıt yapılamadığı doğrulanır");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(5);
        });
        StepTracker.executeStep("Enter registered username", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("username1as"));
        });
        StepTracker.executeStep("Enter new email", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("yeniEmail"));
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
        });
        StepTracker.executeStep("Verify registered data error is displayed", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.kayitliBilgiHataAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc03_kayitliEmailSignUpOlma() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC03 Kayıtlı Email ile Kayıt Testi",
                "Kayıtlı email ile yeni kayıt yapılamadığı doğrulanır");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(5);
        });
        StepTracker.executeStep("Enter new username", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername"));
        });
        StepTracker.executeStep("Enter registered email", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("signInEmail1"));
        });
        StepTracker.executeStep("Enter a password in the password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
        });
        StepTracker.executeStep("Verify registered data error is displayed", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.kayitliBilgiHataAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc04_withoutPasswordNotSignup() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Faker faker = new Faker();
        extentTest = ExtentReport.extentReports.createTest("TC04 Şifresiz Kayıt Testi",
                "Şifre olmadan kayıt yapılamadığı doğrulanır");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        });
        StepTracker.executeStep("Click on the link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(5);
        });
        StepTracker.executeStep("Enter username and email", () -> {
            anasayfa.usernameAs.click();
            anasayfa.usernameAs.sendKeys(faker.name().firstName(), Keys.TAB, faker.internet().emailAddress());
        });
        StepTracker.executeStep("Leave password empty", () -> {
        });
        StepTracker.executeStep("I agree to privacy policy check box", () -> {
            anasayfa.iAgreeButonAs.click();
        });
        StepTracker.executeStep("Click on Sign up button", () -> {
            anasayfa.signUpYeniKayit.click();
        });
        StepTracker.executeStep("Verify registration is not successful", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc05_loginWithRegisteredPassword() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC05 Login with registered user password",
                "Login with valid credentials succeeds");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed() || anasayfa.signInAs.isDisplayed());
        });
        StepTracker.executeStep("Click on Sign in link", () -> {
            anasayfa.signInAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Enter registered username or email", () -> {
            anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("us02LoginUser"));
        });
        StepTracker.executeStep("Enter a password to password box", () -> {
            anasayfa.signInPassword.sendKeys(ConfigReader.getProperty("us02LoginPassword"));
        });
        StepTracker.executeStep("Click login", () -> {
            anasayfa.loginButtonAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Verify that the entry process takes place", () -> {
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.signOutAs.isDisplayed() || anasayfa.myAccountYazisiAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc06_loginWithUnregisteredPassword() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC06 Login with unregistered user password",
                "Login with wrong password shows error");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.signInAs.isDisplayed());
        });
        StepTracker.executeStep("Click on Sign in link", () -> {
            anasayfa.signInAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Enter registered username or email", () -> {
            anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("us02LoginUser"));
        });
        StepTracker.executeStep("Enter wrong password", () -> {
            anasayfa.signInPassword.sendKeys(ConfigReader.getProperty("us02WrongPassword"));
        });
        StepTracker.executeStep("Click login", () -> {
            anasayfa.loginButtonAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Verify that the input process does not occur", () -> {
            ReusableMethods.bekle(1);
            softAssert.assertTrue(anasayfa.loginErrorAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc07_signInWithoutPasswordNotClickable() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC07 Sign in without password",
                "Sign in without password shows fill out error");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.signInAs.isDisplayed());
        });
        StepTracker.executeStep("Click on Sign in link", () -> {
            anasayfa.signInAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Enter email address", () -> {
            anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"));
        });
        StepTracker.executeStep("Leave password empty", () -> {
        });
        StepTracker.executeStep("Click login", () -> {
            anasayfa.loginButtonAs.click();
            ReusableMethods.bekle(1);
        });
        StepTracker.executeStep("Verify fill out error is displayed", () -> {
            softAssert.assertTrue(anasayfa.loginFillOutAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc08_signInWithoutEmailNotAccepted() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC08 Sign in without email",
                "Sign in without email shows fill out error");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.signInAs.isDisplayed());
        });
        StepTracker.executeStep("Click on Sign in link", () -> {
            anasayfa.signInAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Leave username or email empty", () -> {
        });
        StepTracker.executeStep("Enter password", () -> {
            anasayfa.signInPassword.sendKeys(ConfigReader.getProperty("us02LoginPassword"));
        });
        StepTracker.executeStep("Click login", () -> {
            anasayfa.loginButtonAs.click();
            ReusableMethods.bekle(1);
        });
        StepTracker.executeStep("Verify fill out error is displayed", () -> {
            softAssert.assertTrue(anasayfa.loginFillOutAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }

    @Test
    public void tc09_signUpWithRegisteredEmailShowsError() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("TC09 Sign up with registered email",
                "An account is already registered with your email address. Please log in.");
        StepTracker.executeStep("Go to Site", () -> {
            driver().get(siteUrl());
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        });
        StepTracker.executeStep("Click on Sign UP link", () -> {
            anasayfa.registerAs.click();
            ReusableMethods.bekle(2);
        });
        StepTracker.executeStep("Enter registered email in username or email box", () -> {
            anasayfa.emailAs.sendKeys(ConfigReader.getProperty("signInEmail1"));
        });
        StepTracker.executeStep("Enter username for form", () -> {
            anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("username1as"));
        });
        StepTracker.executeStep("Enter a password to password box", () -> {
            anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
        });
        StepTracker.executeStep("Click Sign up and verify already registered message", () -> {
            anasayfa.iAgreeButonAs.click();
            anasayfa.signUpYeniKayit.click();
            ReusableMethods.bekle(2);
            softAssert.assertTrue(anasayfa.kayitliBilgiHataAs.isDisplayed());
        });
        softAssert.assertAll();
        if (!WebDriverManager.isDriverReady())
            Driver.closeDriver();
    }
}
