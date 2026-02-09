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

    @FindBy(xpath = "//*[@class='register inline-type']")
    public WebElement registerAs;

    @FindBy(xpath = "//*[@name='register']")
    public WebElement signUpYeniKayit;

    @FindBy(xpath = "//*[@id='yith-wcwl-message']")
    public WebElement popUpMesajAs;

    @FindBy(xpath = "//*[@id='reg_username']")
    public WebElement usernameAs;

    @FindBy(xpath = "//*[@id='reg_email']")
    public WebElement emailAs;

    @FindBy(xpath = "//*[@id='reg_password']")
    public WebElement passwordSignUpAs;

    @FindBy(xpath = "//*[@id='register-policy']")
    public WebElement iAgreeButonAs;

    @FindBy(xpath = "(//*[@class='submit-status'])[2]")
    public WebElement kayitliBilgiHataAs;

    @FindBy(xpath = "//*[@id='status-1']")
    public WebElement registerValidationAs;

    @FindBy(xpath = "//*[@class='w-icon-account']")
    public WebElement signInAs;

    @FindBy(xpath = "//*[@id='username']")
    public WebElement signInEmail;

    @FindBy(xpath = "//*[@id='password']")
    public WebElement signInPassword;

    @FindBy(xpath = "//*[@name='login']")
    public WebElement loginButtonAs;

    @FindBy(xpath = "//*[@id='login-error']")
    public WebElement loginErrorAs;

    @FindBy(xpath = "//*[@id='login-fillout']")
    public WebElement loginFillOutAs;

    @FindBy(xpath = "//*[@class='page-title']")
    public WebElement myAccountYazisiAs;

    @FindBy(xpath = "//*[@class='login logout inline-type']")
    public WebElement signOutAs;

    @FindBy(xpath = "//a[contains(@href,'edit-address') or @id='link-addresses']")
    public WebElement adressesAs;

    @FindBy(xpath = "//*[@class='icon-box-title text-normal']")
    public WebElement adressesYazi;

    @FindBy(xpath = "(//*[@class='edit btn btn-link btn-primary btn-underline mb-4'])[1]")
    public WebElement billingAdresADD;

    @FindBy(xpath = "//*[@for='billing_first_name']")
    public WebElement firstNameYazi;

    @FindBy(xpath = "//*[@name='billing_first_name']")
    public WebElement adressesFirstNameAs;

    @FindBy(xpath = "//*[@name='billing_last_name']")
    public WebElement adressesLastNameAs;

    @FindBy(xpath = "//*[@id='billing_state_field']")
    public WebElement stateCountAs;

    @FindBy(xpath = "(//*[@aria-owns='select2-billing_state-results'])")
    public WebElement stateCounttextAs;

    @FindBy(xpath = "//*[@name='billing_address_1']")
    public WebElement streetAdressAs;

    @FindBy(xpath = "//*[@name='billing_city']")
    public WebElement townCityAs;

    @FindBy(xpath = "//*[@name='billing_postcode']")
    public WebElement postcode_zipAs;

    @FindBy(xpath = "//*[@name='billing_phone']")
    public WebElement phoneAs;

    @FindBy(xpath = "//*[@class='btn btn-dark btn-rounded btn-sm']")
    public WebElement saveAdressButonAs;

    @FindBy(xpath = "//*[@class='woocommerce-error' or @id='billing-error']")
    public WebElement fieldErrorAs;

    @FindBy(xpath = "//*[contains(@href,'billing') or @id='link-edit-billing']")
    public WebElement editBillingButonAs;

    @FindBy(xpath = "//*[@value='jüpiter']")
    public WebElement firstNameEditBilling;

    @FindBy(xpath = "//*[@value='neptün']")
    public WebElement lastnameEditBilling;

    @FindBy(xpath = "//*[@value='jossiel.lavante@feerock.com']")
    public WebElement emailValue2;

    @FindBy(xpath = "//*[@class='woocommerce-message' or @id='billing-success']")
    public WebElement addressSuccessMessageAs;
}
