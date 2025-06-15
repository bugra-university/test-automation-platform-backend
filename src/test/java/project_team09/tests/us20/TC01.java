package project_team09.tests.us20;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class TC01 extends ExtentReport {

    @Test
    public void test01() {
        MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();
        Orders orders = new Orders();

        extentTest = extentReports.createTest("Vendor olarak Kupon kodu kullanip alisveris yapabilme", "Test Raporu");

        // 1-Go to allovercommerce site
        Driver.getDriver().get(ConfigReader.getProperty("https://allovercommerce.com/"));

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

        // 8-click on the cart icon
        orders.cart.click();
        extentTest.info("Sepet ikonuna tiklandi");

        // 9-click on the checkout button
        orders.checkout.click();
        extentTest.info("Checkout butonuna tiklandi");

        // 10-Click on Enter your code
        orders.CODE.click();
        extentTest.info("Kod girme alanina tiklandi");

        // 11-Enter invalid coupon code
        orders.cuponcodehere.sendKeys("Invalidcode");
        ReusableMethods.bekle(2);
        extentTest.info("Gecersiz kod girildi");

        // 12-click apply coupon button
        orders.aplycoupon.click();
        extentTest.info("Apply butonuna tiklandi");

        // 13-"Coupon code applied successfully." yazısı görüldüğünü
        // ve ürün fiyatında indirim uygulandığını doğrula

        // daha sonra "CART TOTAL" menüsündeki gerekli bilgileri girerek
        // "PROCED TO CHECKOUT" butonuna tıkla (US_06)
        orders.totalcart.sendKeys();

        // açılan sayfada "BILLING DETAILS" menüsündeki gerekli bilgileri girerek
        // "PLACE ORDER" butonuna tıkla (US_17)

        // alışveriş işleminin tamamlandığını (US_17) ve ,
        // "Thank you. Your order has been received." yazısının görüntülendiğini doğrula

    }
}
