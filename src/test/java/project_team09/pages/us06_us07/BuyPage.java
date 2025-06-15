package project_team09.pages.us06_us07;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class BuyPage {

    public BuyPage () {
        PageFactory.initElements(Driver.getDriver(),this);
    }

    @FindBy(xpath = "//*[@for='payment_method_bacs']")
    public WebElement eftYazisi;

    @FindBy(xpath = "//*[@for='payment_method_cod']")
    public WebElement payYazisi;

    @FindBy(xpath = "//*[@id='payment_method_bacs']")
    public WebElement eftSelect;

    @FindBy(xpath = "(//li//input)[3]")
    public WebElement paySelect;

    @FindBy(xpath = "//*[text()='Place order']")
    public WebElement placeOrder;

    @FindBy(xpath = "//*[text()='Thank you. Your order has been received.']")
    public WebElement buyDogrulama;
}
