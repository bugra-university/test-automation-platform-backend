package project_team09.tests.us08;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import project_team09.pages.us08_09.Shopping;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import java.time.Duration;

public class TC_01_TC02_TC03_TC04 {
    protected WebDriver driver;
    protected Actions actions;
    protected Shopping shopping;
    protected SoftAssert softAssert;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = Driver.getDriver();
        actions = new Actions(driver);
        shopping = new Shopping();
        softAssert = new SoftAssert();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(ConfigReader.getProperty("allowerCommerceUrlMe"));
        login();
    }

    @AfterMethod
    public void tearDown() {
        softAssert.assertAll();
        Driver.closeDriver();
    }

    private void login() {
        wait.until(ExpectedConditions.elementToBeClickable(shopping.singINHb)).click();
        String username = ConfigReader.getProperty("userhb");
        String password = ConfigReader.getProperty("passwordhb");
        shopping.userNameOrEmailhb.sendKeys(username, Keys.TAB, password, Keys.ENTER);
        wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOf(shopping.searchBoxHb)));
    }

    @Test
    public void testAddProductsToWishlist() {
        // İlk ürün arama ve wishlist'e ekleme
        searchAndAddToWishlist(ConfigReader.getProperty("urun"));

        // İkinci ürün arama ve wishlist'e ekleme
        searchAndAddToWishlist(ConfigReader.getProperty("urun2"));

        // Wishlist'e git
        wait.until(ExpectedConditions.elementToBeClickable(shopping.whishListButtonHb)).click();

        // Doğrulamalar
        softAssert.assertTrue(shopping.whishListverify1.isDisplayed(), "İlk ürün wishlist'te görünmüyor");
        softAssert.assertTrue(shopping.whishListverify2.isDisplayed(), "İkinci ürün wishlist'te görünmüyor");
    }

    @Test
    public void testQuickViewFunctionality() {
        // Wishlist'e git
        wait.until(ExpectedConditions.elementToBeClickable(shopping.whishListButtonHb)).click();

        // İlk ürün quick view
        wait.until(ExpectedConditions.elementToBeClickable(shopping.quickyViewfirst)).click();
        softAssert.assertTrue(shopping.whishListverify1.isDisplayed(), "İlk ürün detayları görünmüyor");

        // İkinci ürün quick view
        wait.until(ExpectedConditions.elementToBeClickable(shopping.quickyViewsecond)).click();
        softAssert.assertTrue(shopping.whishListverify2.isDisplayed(), "İkinci ürün detayları görünmüyor");
    }

    @Test
    public void testCheckoutProcess() {
        // Wishlist'ten sepete ekleme
        wait.until(ExpectedConditions.elementToBeClickable(shopping.whishListButtonHb)).click();
        wait.until(ExpectedConditions.elementToBeClickable(shopping.addToCardHb)).click();
        wait.until(ExpectedConditions.elementToBeClickable(shopping.viewCartHb)).click();

        // Checkout işlemi
        wait.until(ExpectedConditions.elementToBeClickable(shopping.ProceedToCheckOutHb)).click();

        // Kişisel bilgileri doldur
        fillPersonalInfo();

        // Siparişi tamamla
        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        // Sipariş onayı kontrolü
        wait.until(ExpectedConditions.visibilityOf(shopping.thankyouYazisi));
        softAssert.assertTrue(shopping.thankyouYazisi.isDisplayed(), "Sipariş onay mesajı görünmüyor");
    }

    private void searchAndAddToWishlist(String product) {
        wait.until(ExpectedConditions.elementToBeClickable(shopping.searchBoxHb))
                .sendKeys(product, Keys.ENTER);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.ilkUrun)).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                product.equals(ConfigReader.getProperty("urun")) ? shopping.firstHeartHb : shopping.secondHeartHb))
                .click();
    }

    private void fillPersonalInfo() {
        shopping.userNameHb.clear();
        shopping.emailEB.clear();

        shopping.userNameHb.sendKeys(
                ConfigReader.getProperty("userhb"),
                Keys.TAB,
                ConfigReader.getProperty("lastnamehb"));

        shopping.streetButonEB.sendKeys(ConfigReader.getProperty("streethb"));
        shopping.TownCity.sendKeys(ConfigReader.getProperty("cityhb"));

        shopping.zipCodeButonuEB.sendKeys(
                ConfigReader.getProperty("zipcodecodehb"),
                Keys.TAB,
                ConfigReader.getProperty("phoneHb"),
                Keys.TAB,
                ConfigReader.getProperty("mailHb"));
    }
}
