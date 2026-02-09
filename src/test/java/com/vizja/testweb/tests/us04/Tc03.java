package com.vizja.testweb.tests.us04;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.vizja.testweb.pages.Us04.ShippingAddressPage;
import com.vizja.testweb.utilities.ConfigReader;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.ExtentReport;
import com.vizja.testweb.utilities.ReusableMethods;
import com.vizja.testweb.utilities.WebDriverManager;

public class Tc03 extends ExtentReport {
        @Test
        public void test01() {
                extentTest = extentReports.createTest("US04", "ShipphingAddressEdit");
                ShipphingAddressEditMethodClass editMethod = new ShipphingAddressEditMethodClass();
                editMethod.signIn();
                ShippingAddressPage shipping = new ShippingAddressPage();
                String firstName = ConfigReader.getProperty("degistirilecekFirstName");
                shipping.firstNameBox.clear();
                shipping.firstNameBox.sendKeys(firstName);
                extentTest.info("FIRST NAME KUTUSUNA FIRST NAME YAZILDI");
                shipping.lastNameBox.clear();
                extentTest.info("LAST NAME KUTUSU BOS BIRAKILDI");
                String companyName = ConfigReader.getProperty("degistirilecekCompanyName");
                shipping.companyNameBox.clear();
                shipping.companyNameBox.sendKeys(companyName);
                ReusableMethods.bekle(2);
                extentTest.info("COMPANY NAME KUTUSUNA COMPANY NAME YAZILDI");
                Actions actions = WebDriverManager.isDriverReady() ? new Actions(WebDriverManager.getDriver())
                                : new Actions(Driver.getDriver());
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                ReusableMethods.bekle(2);
                WebElement ddmCountry = shipping.countryDdm;
                Select select = new Select(ddmCountry);
                select.selectByVisibleText(ConfigReader.getProperty("degistirilecekCountry"));
                extentTest.info("COUNTRY DDM DEN SECIM YAPILDI");
                String addressIlkBox = ConfigReader.getProperty("degistirilecekAdressIlkBox");
                shipping.streetAddressFirstBox.clear();
                shipping.streetAddressFirstBox.sendKeys(addressIlkBox);
                ReusableMethods.bekle(2);
                extentTest.info("STREET ADRESS BOLUMUNDEKI ILK KUTU DOLDURULDU");
                String addressIkinciBox = ConfigReader.getProperty("degistirilecekAdresIkinciBox");
                shipping.streetAddressSeccondBox.clear();
                shipping.streetAddressSeccondBox.sendKeys(addressIkinciBox);
                extentTest.info("STREET ADRESS BOLUMUNDEKI IKINCI KUTU DOLDURULDU");
                String postcode = ConfigReader.getProperty("degistirilecekPostcode");
                ReusableMethods.bekle(2);
                shipping.postcodeBox.clear();
                ReusableMethods.bekle(2);
                shipping.postcodeBox.sendKeys(postcode);
                extentTest.info("POSTCODE KUTUSUNA POSTCODE YAZILDI");
                String town = ConfigReader.getProperty("degistirilecekTown");
                shipping.townBox.clear();
                ReusableMethods.bekle(2);
                shipping.townBox.sendKeys(town);
                extentTest.info("TOWN/CITY BOLUMUNE CITY YAZILDI");
                Select select1 = new Select(shipping.provinceDdm);
                ReusableMethods.bekle(2);
                select1.selectByVisibleText(ConfigReader.getProperty("degistirilecekProvince"));
                extentTest.info("PROVINCE DDM DEN SECIM YAPILDI");
                shipping.saveAddressButton.click();
                extentTest.info("SAVE ADRESS BUTONUNA TIKLANDI");
                String expectedMsj = "Last name is a required field.";
                String actualMsj = (WebDriverManager.isDriverReady() ? WebDriverManager.getDriver()
                                : Driver.getDriver()).findElement(By.xpath("//ul[@class='woocommerce-error']"))
                                .getText();
                Assert.assertEquals(expectedMsj, actualMsj);
                extentTest.info("Last name is a required field. MESAJININ CIKTIGI DOGRULANDI");
        }
}
