package com.vizja.testweb.tests.us04;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import com.vizja.testweb.pages.Us04.ShippingAddressPage;
import com.vizja.testweb.utilities.ConfigReader;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.ExtentReport;
import com.vizja.testweb.utilities.ReusableMethods;
import com.vizja.testweb.utilities.WebDriverManager;
public class ShipphingAddressEditMethodClass extends ExtentReport {
        public void signIn() {
                extentTest = extentReports.createTest("US04", "ShipphingAddressEdit");
                String url = ConfigReader.getProperty("allovercommerceUrl");
                if (url == null || url.trim().isEmpty()) {
                        url = ConfigReader.getProperty("allowerCommerceUrl");
                }
                if (WebDriverManager.isDriverReady()) {
                        WebDriverManager.getDriver().get(url);
                } else {
                        Driver.getDriver().get(url);
                }
                extentTest.info("SITEYE GIDILDI");
                ShippingAddressPage shipping = new ShippingAddressPage();
                shipping.signInButton.click();
                extentTest.info("SIGNIN BUTONUNA TIKLA");
                String username = ConfigReader.getProperty("usernamemeMo");
                String password = ConfigReader.getProperty("passwordMo");
                shipping.usernameButton.sendKeys(username, Keys.TAB, password, Keys.ENTER);
                ReusableMethods.bekle(2);
                if (WebDriverManager.isDriverReady()) {
                        WebDriverManager.getDriver().navigate().refresh();
                } else {
                        Driver.getDriver().navigate().refresh();
                }
                extentTest.info("ACILAN SAYFADA USERNAME KUTUSUNA USERNAME, PASSWORD KUTUSUNA PASSWORD YAZILDI VE SIGNIN BUTONUNA TIKLANDI");
                shipping.signOut.click();
                extentTest.info("ACILAN SAYFADA SAG USTTEKI SIGNOUT SEKMESINA TIKLANDI,MYACCONT SAYFASI ACILDI");
                shipping.addressesButton.click();
                extentTest.info("ACILAN SAYFADAKI ADDRESS ELEMENTINE TIKLANDI");
                Actions actions;
                if (WebDriverManager.isDriverReady()) {
                        actions = new Actions(WebDriverManager.getDriver());
                } else {
                        actions = new Actions(Driver.getDriver());
                }
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                ReusableMethods.bekle(2);
                shipping.editYourShippingAddressButton.click();
                extentTest.info("EDİT YOUR SHİPPİNG ADDRESS BUTONUNA TIKLANDI");
        }
}

