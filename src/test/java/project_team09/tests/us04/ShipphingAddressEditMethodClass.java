package project_team09.tests.us04;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import project_team09.pages.meryem.ShippingAddressPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class ShipphingAddressEditMethodClass extends ExtentReport {
    public void signIn() {
        extentTest =extentReports.createTest("US04","ShipphingAddressEdit");
 //stiye git
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceUrl"));
        extentTest.info("SITEYE GIDILDI");
//sign ın butonuna tıkla
        ShippingAddressPageMeryem shipping = new ShippingAddressPageMeryem();
        shipping.signInButton.click();
        extentTest.info("SIGNIN BUTONUNA TIKLA");
//acılan ekranda username  kutusuna username'i gir
//password kutusuna password'u gir
//sign ın butonuna tıkla
        String username = ConfigReader.getProperty("usernamemeMo");
        String password = ConfigReader.getProperty("passwordMo");
        shipping.usernameButton.sendKeys(username, Keys.TAB, password, Keys.ENTER);
        ReusableMethods.bekle(2);
        Driver.getDriver().navigate().refresh();
        extentTest.info("ACILAN SAYFADA USERNAME KUTUSUNA USERNAME, PASSWORD KUTUSUNA PASSWORD YAZILDI VE SIGNIN BUTONUNA TIKLANDI");

        /*
        testcase excell de aşadaki step eksik
         */
// acılan sayfadaki sag üstteki signout sekmesine tıkla.(My accont sayfası acılmalı)
        shipping.signOut.click();//My Account sayfası acılacak
        extentTest.info("ACILAN SAYFADA SAG USTTEKI SIGNOUT SEKMESINA TIKLANDI,MYACCONT SAYFASI ACILDI");
//acılan sayfadaki "Adresses" elementine tıkla
        shipping.addressesButton.click();
        extentTest.info("ACILAN SAYFADAKI ADDRESS ELEMENTINE TIKLANDI");
//Shipping Address başlığının altındaki "ADD"/"Edit Your Shipping Address" butonuna tıkla
        //webelementi görmesi için scroll yapmalıyız.
        Actions actions = new Actions(Driver.getDriver());
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);
        shipping.editYourShippingAddressButton.click();
        extentTest.info("EDİT YOUR SHİPPİNG ADDRESS BUTONUNA TIKLANDI");
    }
}
