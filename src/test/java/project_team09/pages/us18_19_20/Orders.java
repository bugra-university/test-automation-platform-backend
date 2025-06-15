package project_team09.pages.us18_19_20;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class Orders {
        public Orders() {
                PageFactory.initElements(Driver.getDriver(), this);
        }

        // Arama kutusu

        @FindBy(xpath = "(//input[@class='form-control'])[1]")
        public WebElement aramaKutusu;

        @FindBy(xpath = "//a[text()='iphone 13']")
        public WebElement urun;

        @FindBy(xpath = "//*[@class='single_add_to_cart_button button alt'][1]")
        public WebElement addToCard;

        @FindBy(xpath = "(//i[@class='w-icon-cart'])[1]")
        public WebElement cart;

        @FindBy(xpath = "//a[@class='button wc-forward']")
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
        public WebElement orders;

        // @FindBy(xpath = "//*[@class='woocommerce-notice woocommerce-notice--success
        // woocommerce-thankyou-order-received order-success']")
        // public WebElement placeOrderVerify;

        @FindBy(xpath = "(//*[@class='woocommerce-button btn btn-default btn-rounded btn-outline btn-sm btn-block view'])[1]")
        public WebElement view;

        @FindBy(xpath = "(//span[@class='woocommerce-Price-amount amount'])[6]")
        public WebElement sonAlisverisVerify;

        @FindBy(xpath = "(//a[@href='https://allovercommerce.com/my-account-2/'])[1]")
        public WebElement myAccountSayfaEnAlt;
        @FindBy(xpath = "//*[text()=' Back To List']")
        public WebElement backToList;

        // US18

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

        // US18 NEW LOCATE
        @FindBy(xpath = "//input[@id='title']")
        public WebElement CODE;

        @FindBy(xpath = "//*[text()='Add New']")
        public WebElement ADDNEW;

        @FindBy(xpath = "//*[@id='expiry_date']")
        public WebElement CUPPONEXPERYDATE;

        @FindBy(xpath = "//*[@class='wcfm-message wcfm-success']")
        public WebElement cupponsuccesfulsavedyazısı;

        @FindBy(xpath = "//*[@id='coupon_code']")
        public WebElement cuponcodehere;

        @FindBy(xpath = "//*[@value='Update cart']")
        public WebElement CARTUPDATE;

        @FindBy(xpath = "//li[@id='menu-item-1079']")
        public WebElement myaccountbutonu;

        @FindBy(xpath = "//*[text()='Addresse]s'][1")
        public WebElement adressbutonu;

        @FindBy(xpath = "//*[@class='edit btn btn-link btn-primary btn-underline mb-4'][1]")
        public WebElement ADDbutonu;

        @FindBy(xpath = "//*[@class='woocommerce-input-wrapper'][1]")
        public WebElement firstname, lastname;

        @FindBy(xpath = "//*[@id='select2-billing_country-container']")
        public WebElement ÜLKEveBÖLGE;

        @FindBy(xpath = "//*[@id='billing_postcode']")
        public WebElement ZİPKODU;

        @FindBy(xpath = "//*[@id='billing_city']")
        public WebElement getTownCity;

        @FindBy(xpath = "//*[@id='select2-billing_country-container']")
        public WebElement PROVINCE;
        @FindBy(xpath = "//*[@id='billing_phone']")
        public WebElement PHONENUMBER;

        @FindBy(xpath = "//*[@id='billing_email']")
        public WebElement EMAİL;

        @FindBy(xpath = "//*[@name='save_address']")
        public WebElement saveadress;

        @FindBy(xpath = "//*[@id='billing_address_1']")
        public WebElement STREETADRESS;

        @FindBy(xpath = "//*[@class='wc_payment_method payment_method_bacs']")
        public WebElement PAYMENTSMETODSWİRWEFT;

        @FindBy(xpath = "//*[@class='wc_payment_method payment_method_cod']")
        public WebElement getPayAtDoor;

        @FindBy(xpath = "//*[@class='btn btn-rounded btn-border-thin btn-outline btn-dark button']")
        public WebElement aplycoupon;
        @FindBy(xpath = "//h3[@class='cart-title'][1]")
        public WebElement totalcart;

        @FindBy(xpath = "//*[@class='checkout-button button alt wc-forward']")
        public WebElement proceedcheckout;
        @FindBy(xpath = "//*[@id='calc_shipping_country']")
        public WebElement country;

        @FindBy(xpath = "//*[@class='icon-box-icon icon-orders']")
        public WebElement orderbutton;

}
