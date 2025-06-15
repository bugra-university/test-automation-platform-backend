package project_team09.pages.us08_09;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class Shopping {

    public Shopping() {
        PageFactory.initElements(Driver.getDriver(), this);
    }

    // User Hopping-SıgnIn @FindBy(xpath = "//*[@class='login inline-type']")
    public WebElement singINHb;

    @FindBy(css = "#username")
    public WebElement userNameOrEmailhb;

    @FindBy(xpath = "//input[@aria-label='Search']")
    public WebElement searchBoxHb;

    @FindBy(xpath = "//*[@data-product-id='14635']")
    public WebElement firstHeartHb;

    @FindBy(xpath = "//*[@data-product-id='24334']")
    public WebElement secondHeartHb;
    @FindBy(xpath = "(//*[@class='product-media'])[2]")
    public WebElement ilkUrun;

    @FindBy(xpath = "(//*[@class='btn btn-dark btn-rounded btn-sm add_to_cart alt'])[1]")
    public WebElement addToCardHb;

    @FindBy(xpath = " (//*[@class='attachment-woocommerce_thumbnail size-woocommerce_thumbnail'])[2]")
    public WebElement ilkUrun2;
    @FindBy(xpath = "//*[@class='w-icon-heart']")
    public WebElement whishListButtonHb;

    @FindBy(xpath = "(//*[@data-product_id='24334'])[1]")
    public WebElement whishListverify1;

    @FindBy(xpath = "(//*[@class='product-name'])[3]")
    public WebElement whishListverify2;

    @FindBy(xpath = "(//*[@class='product-thumbnail'])[1]")
    public WebElement productverify;

    @FindBy(xpath = "(//*[@class='btn btn-quickview btn-outline btn-default btn-rounded btn-sm mr-lg-2'])[1]")
    public WebElement quickyViewfirst;

    @FindBy(xpath = "(//*[@class='btn btn-quickview btn-outline btn-default btn-rounded btn-sm mr-lg-2'])[2]")
    public WebElement quickyViewsecond;
    @FindBy(xpath = "//*[@class='btn btn-success btn-md']")
    public WebElement viewCartHb;

    @FindBy(xpath = "//*[@class='checkout-button button alt wc-forward']")
    public WebElement ProceedToCheckOutHb;

    @FindBy(xpath = "(//*[@class='input-text '])[1]")
    public WebElement userNameHb;
    @FindBy(xpath = "//*[@id='billing_first_name']")
    public WebElement lasttNameEB;
    @FindBy(xpath = "//*[@id='select2-billing_country-container']")
    public WebElement countryButonEB;
    @FindBy(xpath = "//*[@id='billing_address_1']")
    public WebElement streetButonEB;
    @FindBy(xpath = "//*[@id='select2-billing_state-container']")
    public WebElement stateButonEB;
    @FindBy(xpath = "//*[@id='billing_postcode']")
    public WebElement zipCodeButonuEB;
    @FindBy(xpath = "//*[@class='input-radio'])[1]")
    public WebElement wireTransferButonuEB;
    @FindBy(xpath = "//*[@value='Place order']")
    public WebElement PlaceorderButonuEB;
    @FindBy(xpath = "(//*[@type='email'])[1]")
    public WebElement emailEB;
    @FindBy(xpath = "//*[text()='Thank you. Your order has been received.']")
    public WebElement thankyouYazisi;
    @FindBy(xpath = "//*[@id='coupon_code']")
    public WebElement couponDiscountField;
    @FindBy(xpath = "(//*[@class='input-text '])[6]")
    public WebElement TownCity;

    @FindBy(xpath = "//*[contains(@class, 'woocommerce-error')]")
    public WebElement errorMessage;

    public WebElement getErrorMessage(String message) {
        String xpath = "//*[contains(@class, 'woocommerce-error') and contains(text(), '" + message + "')]";
        return Driver.getDriver().findElement(By.xpath(xpath));
    }

    // Required field errors
    @FindBy(xpath = "//*[contains(text(), 'Billing first name is a required field')]")
    public WebElement firstNameRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Billing last name is a required field')]")
    public WebElement lastNameRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Billing street address is a required field')]")
    public WebElement streetAddressRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Billing postcode is a required field')]")
    public WebElement postcodeRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Billing town / city is a required field')]")
    public WebElement townCityRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Billing state / province is a required field')]")
    public WebElement provinceRequiredError;
}