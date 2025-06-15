package project_team09.pages.meryem;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class AccountDetailsPageMeryem {
    public AccountDetailsPageMeryem() {
        PageFactory.initElements(Driver.getDriver(), this);
    }

    @FindBy(xpath = "//span[text()='Sign In']")
    public WebElement signInButton;
    @FindBy(xpath = "(//input[@type='text'])[1]")
    public WebElement usernameButton;
    @FindBy(xpath = "//span[text()='Sign Out']")
    public WebElement signOut;

    @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/edit-account/'])[1]")
    public WebElement accountDetailsButton;

    @FindBy(xpath = "(//*[text()='My Account'])[2]")
    public WebElement myAccountButton;

    @FindBy(xpath = "(//h4)[2]")
    public WebElement accountDetailsBaslik;

    @FindBy(xpath = "(//input)[3]")
    public WebElement firstName;

    @FindBy(xpath = "(//input)[4]")
    public WebElement lastName;

    @FindBy(xpath = "(//input)[5]")
    public WebElement displayName;

    @FindBy(xpath = "(//input)[6]")
    public WebElement emailAddress;

    @FindBy(xpath = "(//input)[20]")
    public WebElement currentPasswword;

    @FindBy(xpath = "(//input)[21]")
    public WebElement newPasswword;

    @FindBy(xpath = "(//input)[22]")
    public WebElement confirmPasswword;

    @FindBy(xpath = "//button[text()='Save changes']")
    public WebElement saveChangesButton;

    @FindBy(xpath = "//*[@role='alert']")
    public WebElement successfulyMesaji;

    @FindBy(id = "user_description_ifr")
    public WebElement ifarmeBiography;

}
