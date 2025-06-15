package project_team09.tests.us19;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.AlloverCommercePage;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class TC01 extends ExtentReport {
    @Test
    public void testCase01() {
        extentTest = extentReports.createTest("Giris yapip Kupon kodu ile alisveris yapma", "Test Raporu");

        MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();
        AlloverCommercePage page = new AlloverCommercePage();

        // 1-Go to allovercommerce site
        Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrl"));
        extentTest.info("AlloverCommerce sitesine gidildi.");

        // 2-Click the Sign in button
        page.signIn.click();
        extentTest.info("Sign in Butonuna tiklandi");
        ReusableMethods.bekle(2);

        // 3-enter the email field
        myAccountPageSÖ.signInUserNameOrEmaill.sendKeys(ConfigReader.getProperty("musteriUsername"));
        extentTest.info("Musteri username'i girildi.");

        // 4-enter the password field
        myAccountPageSÖ.signInPassword.sendKeys(ConfigReader.getProperty("musteriSifre"));
        extentTest.info("Musteri sifre girildi");
        ReusableMethods.bekle(2);

        // 5-press the sign in button
        page.signIn.click();
        extentTest.info("Sign in Butonuna tiklandi");
        ReusableMethods.bekle(2);

        // 6-Search product and press enter
        page.aramaKutusuMAK.sendKeys(ConfigReader.getProperty("aratilacakUrunveKuponu"), Keys.ENTER);
        extentTest.info("Urun aratildi ");

        // 7-Click on the Add to cart button
        page.addToCartButonuMAK.click();
        extentTest.info("Sepete ekle butonuna basildi");

        // 8-click on the cart icon
        page.cartIkonuMAK.click();
        extentTest.info("Sepet ikonuna tiklandi");

        // 9-click on the checkout button
        page.checkoutButonuMAK.click();
        extentTest.info("Checkout butonuna tiklandi");

        // 10-Click on Enter your code
        page.enterYourCodeButonuMAK.click();
        extentTest.info("Kod girme alanina tiklandi");

        // 11-Enter invalid coupon code
        page.couponCodeYazmaAlaniMAK.sendKeys("Invalidcode");
        ReusableMethods.bekle(2);
        extentTest.info("Gecersiz kod girildi");

        // 12-click apply coupon button
        page.applyCouponButonuMAK.click();
        extentTest.info("Apply butonuna tiklandi");

        // 13-Verify that Coupon "Invalidcode" does not exist!
        Assert.assertEquals(page.couponMessageVerifyMAK.getText(), "Coupon \"invalidcode\" does not exist!");
        Driver.getDriver().navigate().refresh();
        extentTest.info("Girdiginiz kupon kodu bulunmuyor mesaji test edildi");

        // 14-Click on Enter your code
        page.enterYourCodeButonuMAK.click();
        extentTest.info("Kod girme alanina tiklandi");

        // 15-Enter Valid Code
        page.couponCodeYazmaAlaniMAK.sendKeys(ConfigReader.getProperty("aratilacakUrunveKuponu"));
        extentTest.info("Gecerli kod girildi");

        // 16-click apply coupon button
        page.applyCouponButonuMAK.click();
        extentTest.info("Apply butonuna tiklandi");

        // 17-Coupon code applied successfully. Verify that it says
        Assert.assertEquals(page.couponMessageVerifyMAK.getText(), "Coupon code applied successfully.");
        extentTest.info("Kupon kodu basariyla eklendi mesajı test edildi");

        // 18-Click on Place order button
        Actions actions = new Actions(Driver.getDriver());
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(5);

        ReusableMethods.tumSayfaResmi("US19_TC01_Kuponİşlevi");
        extentTest.info("Kodun yaptigi indirim SS alindi");
        page.placeOrderButonuMAK.click();
        ReusableMethods.bekle(25);

        extentTest.info("Place order butonuna tiklandi");

        // 19-"Thank you. Your order has been received." Verify that it says
        Assert.assertEquals(page.orderCompleteVerifyMAK.getText(), "Thank you. Your order has been received.");
        extentTest.info("Siparisiniz alinmistir mesaji test edildi");

        // Close the window
        Driver.quitDriver();
        extentTest.info("Sayfa kapatildi");
    }
}