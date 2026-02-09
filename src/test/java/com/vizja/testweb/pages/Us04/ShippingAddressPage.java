package com.vizja.testweb.pages.Us04;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.WebDriverManager;

public class ShippingAddressPage {
    public ShippingAddressPage() {
        if (WebDriverManager.isDriverReady()) {
            PageFactory.initElements(WebDriverManager.getDriver(), this);
        } else {
            PageFactory.initElements(Driver.getDriver(), this);
        }
    }

    @FindBy(xpath = "//span[text()='Sign In']")
    public WebElement signInButton;

    @FindBy(xpath = "//span[text()='Sign Out']")
    public WebElement signOut;

    @FindBy(xpath = "//a[contains(@href,'my-account')]")
    public WebElement usernameButton;

    @FindBy(id = "username")
    public WebElement usernameBox;

    @FindBy(id = "password")
    public WebElement passwordBox;

    @FindBy(name = "login")
    public WebElement loginButton;

    // Navigation and Titles
    @FindBy(xpath = "//a[text()='Addresses']")
    public WebElement addressesButton;

    @FindBy(xpath = "//h3")
    public WebElement myAccountTitle;

    @FindBy(xpath = "//h3[contains(text(),'Addresses')]")
    public WebElement addressesPageTitle;

    @FindBy(xpath = "//h3[contains(text(),'Shipping Address')]")
    public WebElement shippingAddressTitle;

    // Edit Link
    @FindBy(xpath = "(//a[contains(@class,'edit') and contains(@href,'shipping')])[1]")
    public WebElement editYourShippingAddressButton;

    // Form Fields
    @FindBy(name = "shipping_first_name")
    public WebElement firstNameBox;

    @FindBy(name = "shipping_last_name")
    public WebElement lastNameBox;

    @FindBy(name = "shipping_company")
    public WebElement companyNameBox;

    @FindBy(id = "shipping_country")
    public WebElement countryDdm;

    @FindBy(name = "shipping_address_1")
    public WebElement streetAddressFirstBox;

    @FindBy(name = "shipping_address_2")
    public WebElement streetAddressSeccondBox;

    @FindBy(name = "shipping_postcode")
    public WebElement postcodeBox;

    @FindBy(name = "shipping_city")
    public WebElement townBox;

    @FindBy(id = "shipping_state")
    public WebElement provinceDdm;

    @FindBy(name = "save_address")
    public WebElement saveAddressButton;

    // Validation Messages
    @FindBy(xpath = "//div[@class='woocommerce-message' or contains(@class,'alert-success')]")
    public WebElement dogrulamaMsj;

    @FindBy(xpath = "//ul[@class='woocommerce-error']")
    public WebElement errorMessage;

    @FindBy(xpath = "//li[@data-id='shipping_first_name']")
    public WebElement firstNameRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_last_name']")
    public WebElement lastNameRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_address_1']")
    public WebElement streetAddressRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_city']")
    public WebElement townCityRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_postcode']")
    public WebElement postcodeRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_country']")
    public WebElement countryRequiredError;

    @FindBy(xpath = "//li[@data-id='shipping_state']")
    public WebElement provinceRequiredError;

}
