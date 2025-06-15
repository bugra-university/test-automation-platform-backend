package project_team09.tests.us19;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class TC04 extends ExtentReport {

    @Test
    public void test01() {
        MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();
        Orders orders = new Orders();

        // extentTest = extentReports.createTest("Vendor olarak Kupon kodu kullanip
        // alisveris yapabilme", "Test Raporu");

        // 1-Go to allovercommerce site
        Driver.getDriver().get(ConfigReader.getProperty("https://allovercommerce.com/"));
        // extent.info("AlloverCommerce sitesine gidildi.");

        // 2-Click the Sign in button
        myAccountPageSÖ.signIn.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign in Butonuna tiklandi");

        // 3-enter the email field

        myAccountPageSÖ.signInUserNameOrEmaill.sendKeys(ConfigReader.getProperty("vendorEmail"));
        extentTest.info("Vendor Email'i girildi.");

        // 4-enter the password field
        myAccountPageSÖ.signInPassword.sendKeys(ConfigReader.getProperty("vendorSifre"));
        extentTest.info("Vendor sifre girildi");

        // 5-press the sign in button
        myAccountPageSÖ.signInButton.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign in Butonuna tiklandi");

        // 6-Search product and press enter
        orders.aramaKutusu.sendKeys(ConfigReader.getProperty("aratilacakUrunveKuponu"), Keys.ENTER);
        extentTest.info("Urun aratildi ");

        // 7-Click on the Add to cart button
        // orders.addToCartButonuMAK.click();
        extentTest.info("Sepete ekle butonuna basildi");

        // 9-cart butonuna click yap
        orders.cart.click();

        // 10- açılan sayfada view cart butonuna tıkla
        orders.viewCart.click();

    }
}
