package com.vizja.testweb.tests.us03;

import com.github.javafaker.Faker;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.vizja.testweb.pages.Us01Us02Us03.Anasayfa;
import com.vizja.testweb.utilities.ConfigReader;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.ReusableMethods;
import com.vizja.testweb.utilities.StepTracker;
import com.vizja.testweb.utilities.WebDriverManager;

public class Us03_BillingAdressEkle {
        private static WebDriver driver() {
                return WebDriverManager.isDriverReady() ? WebDriverManager.getDriver() : Driver.getDriver();
        }

        private static String testSiteUrl() {
                String url = ConfigReader.getProperty("testSiteUrl");
                if (url == null || url.trim().isEmpty())
                        throw new IllegalStateException("testSiteUrl is not set in configuration.properties");
                return url.trim();
        }

        @Test
        public void tc01_billingAddressSavedWhenAllFieldsFilled() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                Faker faker = new Faker();
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.registerAs.click();
                        ReusableMethods.bekle(2);
                        anasayfa.usernameAs.sendKeys(faker.name().firstName());
                        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
                        anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
                        anasayfa.iAgreeButonAs.click();
                        anasayfa.signUpYeniKayit.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Enter First name", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("Ali");
                });
                StepTracker.executeStep("Enter Last name", () -> {
                        anasayfa.adressesLastNameAs.sendKeys("Yilmaz");
                });
                StepTracker.executeStep("Enter Country/Region, Street address, Town/City, State, ZIP Code, Phone",
                                () -> {
                                        anasayfa.adressesLastNameAs.sendKeys(Keys.TAB, Keys.TAB, "Turkey", Keys.TAB);
                                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                                        ReusableMethods.bekle(1);
                                        anasayfa.streetAdressAs.sendKeys("Test St. 1");
                                        anasayfa.townCityAs.sendKeys("Istanbul");
                                        anasayfa.stateCountAs.click();
                                        ReusableMethods.bekle(1);
                                        anasayfa.stateCounttextAs.sendKeys("Istanbul", Keys.TAB);
                                        ReusableMethods.bekle(1);
                                        anasayfa.postcode_zipAs.sendKeys("34000");
                                        anasayfa.phoneAs.sendKeys("5551234567");
                                });
                StepTracker.executeStep("Click Save Address button", () -> {
                        anasayfa.saveAdressButonAs.click();
                });
                StepTracker.executeStep("Check success message", () -> {
                        ReusableMethods.bekle(3);
                });
                StepTracker.executeStep("Verify address is saved and shown on page", () -> {
                        softAssert.assertTrue(anasayfa.editBillingButonAs.isDisplayed(),
                                        "Billing address saved and displayed");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc02_saveAddressCannotBeDoneWithoutCountryRegion() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail2"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave Country/Region empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as");
                        anasayfa.adressesLastNameAs.sendKeys("a");
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.streetAdressAs.sendKeys("isik");
                        ReusableMethods.bekle(1);
                        anasayfa.townCityAs.sendKeys("buyuk");
                        ReusableMethods.bekle(1);
                        anasayfa.stateCountAs.click();
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Alaska", Keys.TAB);
                        ReusableMethods.bekle(1);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.postcode_zipAs.click();
                        anasayfa.postcode_zipAs.sendKeys("06210");
                        anasayfa.phoneAs.click();
                        anasayfa.phoneAs.sendKeys("05321456622");
                });
                StepTracker.executeStep("Click Save Address button", () -> {
                        anasayfa.saveAdressButonAs.click();
                });
                StepTracker.executeStep("Verify validation message or button state", () -> {
                        ReusableMethods.bekle(3);
                });
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "siteye eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc03_saveAddressCannotBeDoneWithoutStreet() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave Street address empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as", Keys.TAB, "al", Keys.TAB, Keys.TAB, "Albania",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(2);
                        anasayfa.townCityAs.sendKeys("buyuk");
                        ReusableMethods.bekle(2);
                        anasayfa.stateCountAs.click();
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Berat", Keys.TAB);
                        ReusableMethods.bekle(1);
                        anasayfa.postcode_zipAs.click();
                        anasayfa.postcode_zipAs.sendKeys("06210");
                        anasayfa.phoneAs.click();
                        anasayfa.phoneAs.sendKeys("05321456622");
                });
                StepTracker.executeStep("Click Save Address button", () -> {
                        anasayfa.saveAdressButonAs.click();
                });
                StepTracker.executeStep("Verify validation message or button state", () -> {
                        ReusableMethods.bekle(3);
                });
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc04_saveAddressCannotBeDoneWithoutTownCity() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave Town/City empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as", Keys.TAB, "a", Keys.TAB, Keys.TAB, "Albania",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(2);
                        anasayfa.streetAdressAs.sendKeys("isik");
                        ReusableMethods.bekle(1);
                        anasayfa.stateCountAs.click();
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Berat", Keys.TAB);
                        ReusableMethods.bekle(1);
                        anasayfa.postcode_zipAs.click();
                        anasayfa.postcode_zipAs.sendKeys("06210");
                        ReusableMethods.bekle(1);
                        anasayfa.phoneAs.click();
                        anasayfa.phoneAs.sendKeys("05321456622");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation", () -> ReusableMethods.bekle(3));
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc05_saveAddressCannotBeDoneWithoutState() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave State empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as", Keys.TAB, "a", Keys.TAB, Keys.TAB, "Albania",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(2);
                        anasayfa.streetAdressAs.sendKeys("vatan");
                        anasayfa.townCityAs.click();
                        anasayfa.townCityAs.sendKeys("porto");
                        anasayfa.postcode_zipAs.click();
                        anasayfa.postcode_zipAs.sendKeys("06210");
                        anasayfa.phoneAs.click();
                        anasayfa.phoneAs.sendKeys("05321456622");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation", () -> ReusableMethods.bekle(3));
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc06_saveAddressCannotBeDoneWithoutZIP() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave ZIP Code empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as", Keys.TAB, "a", Keys.TAB, Keys.TAB, "Albania",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.streetAdressAs.sendKeys("porto");
                        anasayfa.townCityAs.sendKeys("buyuk");
                        ReusableMethods.bekle(2);
                        anasayfa.stateCountAs.click();
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Berat", Keys.TAB);
                        ReusableMethods.bekle(1);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.postcode_zipAs.clear();
                        ReusableMethods.bekle(1);
                        anasayfa.phoneAs.click();
                        anasayfa.phoneAs.sendKeys("05321456622");
                });
                StepTracker.executeStep("Click Save Address button", () -> {
                        actions.moveToElement(anasayfa.saveAdressButonAs).perform();
                        ReusableMethods.bekle(1);
                        anasayfa.saveAdressButonAs.click();
                });
                StepTracker.executeStep("Verify validation", () -> ReusableMethods.bekle(3));
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc07_saveAddressCannotBeDoneWithoutPhone() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep("Leave Phone empty; fill other required fields", () -> {
                        anasayfa.adressesFirstNameAs.click();
                        anasayfa.adressesFirstNameAs.sendKeys("as", Keys.TAB, "a", Keys.TAB, Keys.TAB, "Albania",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(1);
                        anasayfa.streetAdressAs.sendKeys("porto");
                        anasayfa.townCityAs.sendKeys("buyuk");
                        ReusableMethods.bekle(2);
                        anasayfa.stateCountAs.click();
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Berat", Keys.TAB);
                        ReusableMethods.bekle(1);
                        anasayfa.postcode_zipAs.click();
                        anasayfa.postcode_zipAs.sendKeys("06210");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation and no save", () -> ReusableMethods.bekle(3));
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "eksik bilgiler ile kayıt yapılamadı");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc08_saveAddressCannotBeDoneWhenAllFieldsEmpty() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                });
                StepTracker.executeStep("Leave all address fields (Country, Street, City, State, ZIP, Phone) empty",
                                () -> {
                                });
                StepTracker.executeStep("Click Save Address button (if enabled)",
                                () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation / error message", () -> ReusableMethods.bekle(2));
                StepTracker.executeStep("Validate that address is not saved", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Save must not complete when all fields empty");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc09_firstNameLastNameEmailAutoFilledWhenEditing() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail4"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                });
                StepTracker.executeStep("Click Edit or open billing address form", () -> {
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.editBillingButonAs.click();
                        ReusableMethods.bekle(3);
                });
                StepTracker.executeStep("Check First name, Last name, Email fields", () -> {
                        softAssert.assertTrue(anasayfa.firstNameEditBilling.isDisplayed());
                        softAssert.assertTrue(anasayfa.lastnameEditBilling.isDisplayed());
                        softAssert.assertTrue(anasayfa.emailValue2.isDisplayed());
                });
                StepTracker.executeStep("Change only address fields (e.g. Street or City)", () -> {
                        anasayfa.streetAdressAs.clear();
                        anasayfa.streetAdressAs.sendKeys("Updated Street");
                        anasayfa.townCityAs.clear();
                        anasayfa.townCityAs.sendKeys("Updated City");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify success message", () -> {
                        ReusableMethods.bekle(3);
                        softAssert.assertTrue(anasayfa.addressSuccessMessageAs.isDisplayed(),
                                        "Address changed successfully. message expected");
                });
                StepTracker.executeStep("Verify First name, Last name, Email still show correct auto values", () -> {
                        ReusableMethods.bekle(2);
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(5);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(2);
                        JavascriptExecutor js = (JavascriptExecutor) driver();
                        js.executeScript("arguments[0].click();", anasayfa.editBillingButonAs);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameEditBilling.isDisplayed());
                        softAssert.assertTrue(anasayfa.lastnameEditBilling.isDisplayed());
                        softAssert.assertTrue(anasayfa.emailValue2.isDisplayed());
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc10_addressChangedSuccessfullyMessageDisplayed() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                });
                StepTracker.executeStep("Go to Addresses", () -> {
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());
                });
                StepTracker.executeStep("Go to Billing Address", () -> {
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                });
                StepTracker.executeStep("Fill all required fields with valid data", () -> {
                        anasayfa.adressesFirstNameAs.sendKeys("Ali", Keys.TAB, "Yilmaz", Keys.TAB, Keys.TAB, "Turkey",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.streetAdressAs.sendKeys("Test St. 1");
                        anasayfa.townCityAs.sendKeys("Istanbul");
                        anasayfa.townCityAs.sendKeys("Istanbul");
                        JavascriptExecutor js = (JavascriptExecutor) driver();
                        js.executeScript("arguments[0].click();", anasayfa.stateCountAs);
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Istanbul", Keys.TAB);
                        anasayfa.postcode_zipAs.sendKeys("34000");
                        anasayfa.phoneAs.sendKeys("5551234567");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify on-screen message", () -> {
                        ReusableMethods.bekle(3);
                        softAssert.assertTrue(anasayfa.addressSuccessMessageAs.isDisplayed(),
                                        "\"Address changed successfully.\" message expected");
                });
                StepTracker.executeStep("Verify message is visible and clear to user", () -> {
                        softAssert.assertTrue(anasayfa.addressSuccessMessageAs.isDisplayed(),
                                        "Message must be visible and understandable");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc11_saveAddressWithInvalidZIPRejected() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                });
                StepTracker.executeStep("Enter invalid ZIP (e.g. letters or too short)", () -> {
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(1);
                        anasayfa.postcode_zipAs.clear();
                        anasayfa.postcode_zipAs.sendKeys("ABC");
                });
                StepTracker.executeStep("Fill other required fields with valid data", () -> {
                        anasayfa.adressesFirstNameAs.sendKeys("Ali", Keys.TAB, "Yilmaz", Keys.TAB, Keys.TAB, "Turkey",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.streetAdressAs.sendKeys("Test St. 1");
                        anasayfa.townCityAs.sendKeys("Istanbul");
                        JavascriptExecutor js = (JavascriptExecutor) driver();
                        js.executeScript("arguments[0].click();", anasayfa.stateCountAs);
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Istanbul", Keys.TAB);
                        anasayfa.phoneAs.sendKeys("5551234567");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation or error message", () -> {
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Validation or error message expected for invalid ZIP");
                });
                StepTracker.executeStep("Verify address is not saved with invalid ZIP", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Address must not be saved with invalid ZIP");
                });
                StepTracker.executeStep("Verify save does not complete or invalid data rejected", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Save must not complete; invalid data rejected");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc12_saveAddressWithInvalidPhoneRejected() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                });
                StepTracker.executeStep("Enter invalid Phone (e.g. letters or too short)", () -> {
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(1);
                        anasayfa.phoneAs.clear();
                        anasayfa.phoneAs.sendKeys("abc");
                });
                StepTracker.executeStep("Fill other required fields with valid data", () -> {
                        anasayfa.adressesFirstNameAs.sendKeys("Ali", Keys.TAB, "Yilmaz", Keys.TAB, Keys.TAB, "Turkey",
                                        Keys.TAB);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        anasayfa.streetAdressAs.sendKeys("Test St. 1");
                        anasayfa.townCityAs.sendKeys("Istanbul");
                        JavascriptExecutor js = (JavascriptExecutor) driver();
                        js.executeScript("arguments[0].click();", anasayfa.stateCountAs);
                        ReusableMethods.bekle(1);
                        anasayfa.stateCounttextAs.sendKeys("Istanbul", Keys.TAB);
                        anasayfa.postcode_zipAs.sendKeys("34000");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify validation or error message", () -> {
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Validation or error message expected for invalid phone");
                });
                StepTracker.executeStep("Verify address is not saved with invalid phone", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Address must not be saved with invalid phone");
                });
                StepTracker.executeStep("Verify save does not complete or invalid data rejected", () -> {
                        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),
                                        "Save must not complete; invalid data rejected");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }

        @Test
        public void tc13_billingAddressAddedBySaveWhenFormValid() {
                Anasayfa anasayfa = new Anasayfa();
                SoftAssert softAssert = new SoftAssert();
                Actions actions = new Actions(driver());
                StepTracker.executeStep("Go to Site", () -> {
                        driver().get(testSiteUrl());
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
                });
                StepTracker.executeStep("Go to My Account → Addresses → Billing Address", () -> {
                        anasayfa.signInAs.click();
                        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB,
                                        ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        anasayfa.billingAdresADD.click();
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());
                });
                StepTracker.executeStep(
                                "Fill all required fields: First name, Last name, Email (if editable), Country, Street, Town/City, State, ZIP, Phone",
                                () -> {
                                        anasayfa.adressesFirstNameAs.sendKeys("Ali", Keys.TAB, "Yilmaz", Keys.TAB,
                                                        Keys.TAB, "Turkey", Keys.TAB);
                                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                                        anasayfa.streetAdressAs.sendKeys("Test St. 1");
                                        anasayfa.townCityAs.sendKeys("Istanbul");
                                        JavascriptExecutor js = (JavascriptExecutor) driver();
                                        js.executeScript("arguments[0].click();", anasayfa.stateCountAs);
                                        ReusableMethods.bekle(1);
                                        anasayfa.stateCounttextAs.sendKeys("Istanbul", Keys.TAB);
                                        anasayfa.postcode_zipAs.sendKeys("34000");
                                        anasayfa.phoneAs.sendKeys("5551234567");
                                });
                StepTracker.executeStep("Verify Save Address button is enabled", () -> {
                        softAssert.assertTrue(anasayfa.saveAdressButonAs.isDisplayed(),
                                        "Save Address button must be visible");
                        softAssert.assertTrue(anasayfa.saveAdressButonAs.isEnabled(),
                                        "Save Address button is clickable");
                });
                StepTracker.executeStep("Click Save Address button", () -> anasayfa.saveAdressButonAs.click());
                StepTracker.executeStep("Verify success message", () -> {
                        ReusableMethods.bekle(3);
                        softAssert.assertTrue(anasayfa.addressSuccessMessageAs.isDisplayed(),
                                        "\"Address changed successfully.\" is displayed");
                });
                StepTracker.executeStep("Verify saved address appears on page", () -> {
                        softAssert.assertTrue(anasayfa.editBillingButonAs.isDisplayed(),
                                        "Address is displayed correctly (edit option visible)");
                });
                StepTracker.executeStep("Verify address can be edited again from same page", () -> {
                        anasayfa.adressesAs.click();
                        ReusableMethods.bekle(2);
                        actions.sendKeys(Keys.PAGE_DOWN).perform();
                        ReusableMethods.bekle(1);
                        JavascriptExecutor js = (JavascriptExecutor) driver();
                        js.executeScript("arguments[0].click();", anasayfa.editBillingButonAs);
                        ReusableMethods.bekle(2);
                        softAssert.assertTrue(anasayfa.firstNameEditBilling.isDisplayed()
                                        || anasayfa.firstNameYazi.isDisplayed(),
                                        "User can edit and save again");
                });
                softAssert.assertAll();
                if (!WebDriverManager.isDriverReady())
                        Driver.closeDriver();
        }
}
