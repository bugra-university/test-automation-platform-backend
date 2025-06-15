package project_team09.pages.us06_us07;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class HomePage {

    public HomePage (){
        PageFactory.initElements(Driver.getDriver(),this);
    }

    @FindBy(xpath = "//*[text()='Sign In']")
    public WebElement SignInButton;

    @FindBy(xpath = "(//*[@*='username'])[2]")
    public WebElement Username;

    @FindBy(xpath = "(//input)[2]")
    public WebElement aramaKutusu;

    @FindBy(xpath = "//*[text()='Search results for “Shoes”']")
    public WebElement aramaSonucu;

    @FindBy(xpath = "(//p)[3]")
    public WebElement NoaramaSonucu;

    @FindBy(xpath = "(//*[@*='attachment-woocommerce_thumbnail size-woocommerce_thumbnail'])[2]")
    public WebElement shoesPicture;

    @FindBy(xpath = "(//*[@class='woocommerce-LoopProduct-link woocommerce-loop-product__link'])[1]")
    public WebElement shirtPicture;

    @FindBy(xpath = "(//*[@*='product-wrap'])[2]")
    public WebElement bagPicture;

    @FindBy(xpath = "(//*[@*='product-media'])[1]")
    public WebElement watchPicture;

    @FindBy(xpath = "(//*[@*='product-media'])[1]")
    public WebElement iphonePicture;

    @FindBy(xpath = "//*[@*='add-to-cart']")
    public WebElement addToCartButton;

    @FindBy(xpath = "//*[@class='woocommerce-message alert alert-simple alert-btn alert-success']")
    public WebElement cartEklemeSonucu;

    @FindBy(xpath = "(//*[@class='w-icon-cart'])[1]")
    public WebElement cartButton;

    @FindBy(xpath = "//*[@*='button wc-forward']")
    public WebElement viewCartButton;

    @FindBy(xpath = "//*[@*='Plus']")
    public WebElement plusButton;

    @FindBy(xpath = "//*[@*='update_cart']")
    public WebElement updateCartButton;

    @FindBy(xpath = "//*[text()='Cart updated.']")
    public WebElement eklemeDogrulama;
    //(//*[@class='quantity'])[2]
    ////*[@type='number']

    @FindBy(xpath = "//*[@*='Minus']")
    public WebElement minusButton;

    @FindBy(xpath = "//*[@*='button checkout wc-forward']")
    public WebElement checkoutButton;



}
