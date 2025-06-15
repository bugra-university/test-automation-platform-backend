package project_team09.pages.us06_us07;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class MyAccount {

    public MyAccount () { PageFactory.initElements(Driver.getDriver(),this); }

    @FindBy(xpath = "(//*[text()='My Account'])[2]")
    public WebElement myAccountButton;

    @FindBy(xpath = "(//*[text()='Addresses'])[1]")
    public WebElement adressesButton;

    @FindBy(xpath = "(//*[@*='edit btn btn-link btn-primary btn-underline mb-4'])[1]")
    public WebElement addButton;

    @FindBy(xpath = "(//*[@*='text'])[1]")
    public WebElement textBox;

    @FindBy(xpath = "(//*[@class='select2-selection__placeholder'])[1]")
    public WebElement textCountry;

    @FindBy(xpath = "(//*[@type='text'])[4]")
    public WebElement textStreet;

    @FindBy(xpath = "(//*[text()='Select an option…'])[2]")
    public WebElement textState;

    @FindBy(xpath = "(//*[@type='text'])[7]")
    public WebElement textZIP;

    @FindBy(xpath = "//*[text()='Address changed successfully.']")
    public WebElement dogrulama;

    @FindBy(xpath = "//*[text()='Edit Your Billing Address']")
    public WebElement editAdresses;
}
