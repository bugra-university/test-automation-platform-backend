package com.vizja.testweb.pages.Us01Us02Us03;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.vizja.testweb.utilities.Driver;
import com.vizja.testweb.utilities.WebDriverManager;

public class Anasayfa {
    public Anasayfa() {
        if (WebDriverManager.isDriverReady()) {
            PageFactory.initElements(WebDriverManager.getDriver(), this);
            System.out.println("[Anasayfa] Initialized with WebDriverManager");
        } else {
            PageFactory.initElements(Driver.getDriver(), this);
            System.out.println("[Anasayfa] Initialized with legacy Driver");
        }
    }

    @FindBy(xpath = "//span[text()='Register']")
    public WebElement registerAs;

    @FindBy(xpath = "//a[contains(text(),'Sign Up')]")
    public WebElement signUpYeniKayit;

    @FindBy(xpath = "//div[@class='wcfm-message email_error']")
    public WebElement popUpMesajAs;

    @FindBy(id = "reg_username")
    public WebElement usernameAs;

    @FindBy(id = "reg_email")
    public WebElement emailAs;

    @FindBy(id = "reg_password")
    public WebElement passwordSignUpAs;

    @FindBy(id = "register-policy")
    public WebElement iAgreeButonAs;

    @FindBy(xpath = "//p[contains(text(),'An account is already registered')]")
    public WebElement kayitliBilgiHataAs;

    @FindBy(xpath = "//div[@class='woocommerce-message']")
    public WebElement registerValidationAs;

    @FindBy(xpath = "//span[text()='Sign In']")
    public WebElement signInAs;

    @FindBy(id = "username")
    public WebElement signInEmail;

    @FindBy(id = "password")
    public WebElement signInPassword;

    @FindBy(name = "login")
    public WebElement loginButtonAs;

    @FindBy(xpath = "//ul[@class='woocommerce-error']")
    public WebElement loginErrorAs;

    @FindBy(xpath = "//ul[@class='woocommerce-error']//li")
    public WebElement loginFillOutAs;

    @FindBy(xpath = "//h2[contains(text(),'My Account')]")
    public WebElement myAccountYazisiAs;

    @FindBy(xpath = "//span[text()='Sign Out']")
    public WebElement signOutAs;

    @FindBy(xpath = "//a[contains(text(),'Addresses')]")
    public WebElement adressesAs;

    @FindBy(xpath = "//h3[contains(text(),'Addresses')]")
    public WebElement adressesYazi;

    @FindBy(xpath = "//a[contains(@href,'billing') and contains(@class,'edit')]")
    public WebElement billingAdresADD;

    @FindBy(xpath = "//label[@for='billing_first_name']")
    public WebElement firstNameYazi;

    @FindBy(id = "billing_first_name")
    public WebElement adressesFirstNameAs;

    @FindBy(id = "billing_last_name")
    public WebElement adressesLastNameAs;

    @FindBy(id = "billing_country")
    public WebElement stateCountAs;

    @FindBy(xpath = "//input[@class='select2-search__field']")
    public WebElement stateCounttextAs;

    @FindBy(id = "billing_address_1")
    public WebElement streetAdressAs;

    @FindBy(id = "billing_city")
    public WebElement townCityAs;

    @FindBy(id = "billing_postcode")
    public WebElement postcode_zipAs;

    @FindBy(id = "billing_phone")
    public WebElement phoneAs;

    @FindBy(name = "save_address")
    public WebElement saveAdressButonAs;

    @FindBy(xpath = "//ul[@class='woocommerce-error']")
    public WebElement fieldErrorAs;

    @FindBy(xpath = "//a[contains(@href,'billing') and contains(@class,'edit')]")
    public WebElement editBillingButonAs;

    @FindBy(id = "billing_first_name")
    public WebElement firstNameEditBilling;

    @FindBy(id = "billing_last_name")
    public WebElement lastnameEditBilling;

    @FindBy(id = "billing_email")
    public WebElement emailValue2;

    @FindBy(xpath = "//div[@class='woocommerce-message']")
    public WebElement addressSuccessMessageAs;
}
