package project_team09.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;


public class MyAccountPageEnsar {
    public MyAccountPageEnsar() {

        PageFactory.initElements(Driver.getDriver(),this);
    }

    @FindBy(xpath = "(//li)[5]")
    public WebElement DashboardButtonLocate;

    @FindBy(xpath = "(//li)[6]")
    public WebElement StoreManagerButtonLocate;

    @FindBy(xpath = "(//li)[7]")
    public WebElement OrdersButtonLocate;

    @FindBy(xpath = "(//li)[8]")
    public WebElement DownloadsButtonLocate;

    @FindBy(xpath = "(//li)[9]")
    public WebElement AddressesButtonLocate;

    @FindBy(xpath = "(//li)[10]")
    public WebElement AccountDetailsButtonLocate;

    @FindBy(xpath = "(//li)[11]")
    public WebElement WishlistButtonLocate;

    @FindBy(xpath = "(//li)[12]")
    public WebElement SupportTicketsButtonLocate;

    @FindBy(xpath = "(//li)[13]")
    public WebElement FollowingsButtonLocate;


    @FindBy(xpath = "(//li)[14]")
    public WebElement LogoutButtonLocate;

    @FindBy(xpath = "//*[@class='page-title']")
    public WebElement MyAccountPageText;



    @FindBy(xpath = "(//*[.='Register'])[2]")
    public WebElement RegisterButtonLocate;

    @FindBy(xpath = "(//h2)[1]")
    public WebElement VendorRegistionTextLocate;

    @FindBy(xpath = "//*[text()='Become a Vendor']")
    public WebElement BecomeVendorButtonLocate;

    @FindBy(id = "password_strength")
    public WebElement verifyPassword;

    @FindBy(xpath = "(//input[@type='password'])[1]")
    public WebElement PasswordTextBoxLocate;

    @FindBy(xpath = "//*[@class='login inline-type']")
    public WebElement SignİnButtonLocate;

    @FindBy(xpath = "//*[@id='customer_login']")
    public WebElement SignInPopUp;

    @FindBy(xpath = "//input[@id='password']")
    public WebElement PopUpİçindePasswordBox;

    @FindBy(xpath = "//button[@value='Sign In']")
    public WebElement PopUpİçindesignInButton;

    @FindBy(xpath = "//*[@id='username']")
    public WebElement PopUpİçindeEmailBox;







}







