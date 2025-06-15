package project_team09.tests.us08;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import project_team09.pages.us08_09.Shopping;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;

import java.time.Duration;

public class TC05_TC06_TC07_TC08_TC09_TC10 {
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
        goToCheckout();
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

    private void goToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(shopping.addToCardHb)).click();
        wait.until(ExpectedConditions.elementToBeClickable(shopping.viewCartHb)).click();
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        wait.until(ExpectedConditions.elementToBeClickable(shopping.ProceedToCheckOutHb)).click();
    }

    @Test
    public void testFirstNameRequired() {
        // Sadece firstname alanını boş bırak
        fillCheckoutForm(true, false, false, false, false, false);

        // Submit the form
        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        // Verify error message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.firstNameRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "First name required error message should be displayed");
    }

    @Test
    public void testLastNameRequired() {
        // Sadece lastname alanını boş bırak
        fillCheckoutForm(false, true, false, false, false, false);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.lastNameRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "Last name required error message should be displayed");
    }

    @Test
    public void testStreetAddressRequired() {
        // Sadece street address alanını boş bırak
        fillCheckoutForm(false, false, true, false, false, false);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.streetAddressRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "Street address required error message should be displayed");
    }

    @Test
    public void testPostCodeRequired() {
        // Sadece postcode alanını boş bırak
        fillCheckoutForm(false, false, false, true, false, false);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.postcodeRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "Post code required error message should be displayed");
    }

    @Test
    public void testTownCityRequired() {
        // Sadece town/city alanını boş bırak
        fillCheckoutForm(false, false, false, false, true, false);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.townCityRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "Town/City required error message should be displayed");
    }

    @Test
    public void testProvinceRequired() {
        // Sadece province alanını boş bırak
        fillCheckoutForm(false, false, false, false, false, true);

        wait.until(ExpectedConditions.elementToBeClickable(shopping.PlaceorderButonuEB)).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOf(shopping.provinceRequiredError));
        softAssert.assertTrue(errorMessage.isDisplayed(), "Province required error message should be displayed");
    }

    private void fillCheckoutForm(boolean skipFirstName, boolean skipLastName, boolean skipStreet,
            boolean skipPostCode, boolean skipTownCity, boolean skipProvince) {

        shopping.emailEB.clear();

        if (!skipFirstName) {
            shopping.userNameHb.sendKeys(ConfigReader.getProperty("userhb"));
        }

        if (!skipLastName) {
            shopping.lasttNameEB.sendKeys(ConfigReader.getProperty("lastnamehb"));
        }

        if (!skipStreet) {
            shopping.streetButonEB.sendKeys(ConfigReader.getProperty("streethb"));
        }

        if (!skipPostCode) {
            shopping.zipCodeButonuEB.sendKeys(ConfigReader.getProperty("zipcodecodehb"));
        }

        if (!skipTownCity) {
            shopping.TownCity.sendKeys(ConfigReader.getProperty("cityhb"));
        }

        // Email ve telefon her durumda doldurulur
        shopping.emailEB.sendKeys(ConfigReader.getProperty("mailHb"));
    }
}
