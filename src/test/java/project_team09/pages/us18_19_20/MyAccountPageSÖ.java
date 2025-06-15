package project_team09.pages.us18_19_20;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class MyAccountPageSÖ {

    public MyAccountPageSÖ() {
        PageFactory.initElements(Driver.getDriver(),this);
    }

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


    @FindBy(xpath = "(//*[.='Register'])[2]")
    public WebElement RegisterButtonLocate;

    @FindBy(xpath = "//*[text()='Become a Vendor']")
    public WebElement BecomeVendorButtonLocate;

    @FindBy(xpath = "(//h2)[1]")
    public WebElement VendorRegistrationText;



    @FindBy(xpath = "//*[text()='Sign In']")
    public WebElement signIn;

    @FindBy(css = "[id=username]")
    public WebElement signInUserNameOrEmaill;

    @FindBy(css = "[id=password]")
    public WebElement signInPassword;

    @FindBy(css = "[name=login]")
    public WebElement signInButton;

    @FindBy(xpath = "(//*[@class='submit-status'])[1]")
    public WebElement yanlisKullaniciVeyaParolaYazisi;

    // register locates
    @FindBy(xpath = "//*[text()='Register']")
    public WebElement register;

    @FindBy(css = "[id=reg_username]")
    public WebElement registerUserName;

    @FindBy(css = "[id=reg_email]")
    public WebElement registerEmailAddress;

    @FindBy(css = "[id=reg_password]")
    public WebElement registerPassword;

    @FindBy(css = "[class=register_as_vendor]")
    public WebElement SignupAsaVendor;

    @FindBy(xpath = "(//input[@type='checkbox'])[2]")
    public WebElement CheckboxIAgree;

    @FindBy(css = "[name=register]")
    public WebElement signUpButton;

    @FindBy(xpath = "//*[text()='Sign Out']")
    public WebElement signOutButton;

    @FindBy(xpath = "//*[@id='customer_login']")
    public WebElement registerIAgreeIsaretleme;

    @FindBy(xpath = "//*[text()='An account is already registered with your email address. ']")
    public WebElement accountAlreadyYazisi;

    @FindBy(xpath = "//*[@class='login-popup']")
    public WebElement popUp;

    @FindBy(xpath = "//*[@class='mfp-content']")
    public WebElement iAgreeKutusuIsaretlemeUyarisi;

    @FindBy(xpath = "//*[text()='An account is already registered with your email address. ']")
    public WebElement varolanMailIleKayitYapamamaYazisi;

    @FindBy(xpath = "//*[text()='An account is already registered with that username. Please choose another.']")
    public WebElement varolanUsernameIleKayitYapamamaYazisi;



    public static class Orders {

        public Orders(){
            PageFactory.initElements(Driver.getDriver(), this);}


        //Arama kutusu

        @FindBy(xpath = "(//input[@class='form-control'])[1]")
        public WebElement aramaKutusu;


        @FindBy(xpath = "//a[text()='iphone 13']")
        public WebElement urun;

        @FindBy(xpath = "//*[@name='add-to-cart']")
        public WebElement addToCard;

        @FindBy(xpath = "(//i[@class='w-icon-cart'])[1]")
        public WebElement cart;

        @FindBy(xpath = "//*[text()='View cart']")
        public WebElement viewCart;


        @FindBy(xpath = "(//*[@class='woocommerce-cart-form__cart-item cart_item'])[1]")
        public WebElement urun1Dogrulama;
        @FindBy(xpath = "(//*[@class='woocommerce-cart-form__cart-item cart_item'])[2]")
        public WebElement urun2Dogrulama;
        @FindBy(xpath = "(//*[@class='woocommerce-cart-form__cart-item cart_item'])[3]")
        public WebElement urun3Dogrulama;

        @FindBy(xpath = "(//*[text()='Checkout'])[1]")
        public WebElement checkout;

        @FindBy(xpath = "//*[@name='billing_first_name']")
        public WebElement firstName;

        @FindBy(xpath = "//*[@name='billing_last_name']")
        public WebElement lastName;

        @FindBy(xpath = "//*[@name='billing_company']")
        public WebElement company;

        @FindBy(xpath = "//select[@name=\"billing_country\"]")
        public WebElement countryRegion;

        @FindBy(xpath = "//*[@name='billing_address_1']")
        public WebElement street1;

        @FindBy(xpath = "//*[@name='billing_address_2']")
        public WebElement street2;

        @FindBy(xpath = "//select[@id='billing_state']")
        public WebElement province;


        @FindBy(xpath = "//*[@id='billing_email']")
        public WebElement mail;

        @FindBy(xpath = "//input[@id='billing_state']")
        public WebElement stateUS17;

        @FindBy(xpath = "//input[@id='billing_postcode']")
        public WebElement zipCodeUS17;

        @FindBy(xpath = "//*[@id='billing_city']")
        public WebElement townCity;

        @FindBy(xpath = "//input[@id='billing_phone']")
        public WebElement phoneUS17;
        @FindBy(xpath = "//tr[@class='order-total']")
        public WebElement total;
        @FindBy(xpath = "(//input[@class='input-radio'])[2]")
        public WebElement payAtDoor;

        @FindBy(xpath = "//button[@id='place_order']")
        public WebElement placeOrder;
        @FindBy(xpath = "(//div[@class='icon-box text-center'])[1]")
        public WebElement ordersUS17;

        //  @FindBy(xpath = "//*[@class='woocommerce-notice woocommerce-notice--success woocommerce-thankyou-order-received order-success']")
        //  public WebElement placeOrderVerify;


        @FindBy(xpath = "(//*[@class='woocommerce-button btn btn-default btn-rounded btn-outline btn-sm btn-block view'])[1]")
        public WebElement view;

        @FindBy(xpath = "(//span[@class='woocommerce-Price-amount amount'])[6]")
        public WebElement sonAlisverisVerify;


        @FindBy(xpath = "(//a[@href='https://allovercommerce.com/my-account-2/'])[1]")
        public WebElement myAccountSayfaEnAlt;
        @FindBy(xpath = "//*[text()=' Back To List']")
        public WebElement backToList;

        //US18

        @FindBy(xpath = "(//*[text()='My Account'])[2]")
        public WebElement myAccountUS18;

        @FindBy(xpath = "//a[@href=\"https://allovercommerce.com/store-manager/\"]")
        public WebElement storeManager;
        @FindBy(xpath = "(//span[@class='wcfmfa fa-gift'])[1]")
        public WebElement coupons;
        @FindBy(xpath = "(//span[@class='wcfmfa fa-gift'])[3]")
        public WebElement addNewCoupon;
        @FindBy(xpath = "(//input[@class='wcfm-text wcfm_ele'])[1]")
        public WebElement code;
        @FindBy(xpath = "//*[@id='description']")
        public WebElement couponDescription;
        @FindBy(xpath = "//*[@id='discount_type']")
        public WebElement discontType;
        @FindBy(xpath = "//*[@id='coupon_amount']")
        public WebElement couponAmount;
        @FindBy(xpath = "//*[@id='expiry_date']")
        public WebElement couponExpiryDate;
        @FindBy(xpath = "//*[@id='free_shipping']")
        public WebElement couponChechBox1;
        @FindBy(xpath = "//*[@id='show_on_store']")
        public WebElement couponChechBox2;
        @FindBy(xpath = "//input[@name='submit-data']")
        public WebElement couponSubmit;
        @FindBy(xpath = "//div[@class='dataTables_info']")
        public WebElement couponVerify;


































    }

}






