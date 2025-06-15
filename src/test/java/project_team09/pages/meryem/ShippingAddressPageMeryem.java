package project_team09.pages.meryem;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class ShippingAddressPageMeryem {
    public ShippingAddressPageMeryem() {
        PageFactory.initElements(Driver.getDriver(), this);
    }

    @FindBy(xpath = "//span[text()='Sign In']")
    public WebElement signInButton;
    @FindBy(xpath = "(//input[@type='text'])[1]")
    public WebElement usernameButton;
    @FindBy(xpath = "//span[text()='Sign Out']")
    public WebElement signOut;

    @FindBy(xpath = "//*[text()='Addresses']")
    public WebElement addressesButton;
    @FindBy(xpath = "(//*[@class='edit btn btn-link btn-primary btn-underline mb-4'])[2]")
    public WebElement editYourShippingAddressButton;

    @FindBy(xpath = "(//input[@class='input-text '])[1]")
    public WebElement firstNameBox;

    @FindBy(xpath = "(//input[@class='input-text '])[2]")
    public WebElement lastNameBox;

    @FindBy(xpath = "(//input[@class='input-text '])[3]")
    public WebElement companyNameBox;

    @FindBy(xpath = "(//select)[1]")
    public WebElement countryDdm;

    @FindBy(xpath = "(//input[@class='input-text '])[4]")
    public WebElement streetAddressFirstBox;
    @FindBy(xpath = "(//input[@class='input-text '])[5]")
    public WebElement streetAddressSeccondBox;

    @FindBy(xpath = "(//input)[8]")
    public WebElement postcodeBox;

    @FindBy(xpath = "(//input[@class='input-text '])[7]")
    public WebElement townBox;

    @FindBy(xpath = "(//select)[2]")
    public WebElement provinceDdm;

    @FindBy(xpath = "(//button)[2]")
    public WebElement saveAddressButton;

    @FindBy(xpath = "//div[@role='alert']")
    public WebElement dogrulamaMsj;

}
