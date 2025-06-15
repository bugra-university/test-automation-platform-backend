package project_team09.pages.us18_19_20;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;





    public class AlloverCommercePage {
        public Alert signInUserNameOrEmail;

        public AlloverCommercePage() {
            PageFactory.initElements(Driver.getDriver(), this);
        }
        //Bahadir ' in locatelerini alacagi alan baslangic

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

        @FindBy(xpath = "//h2[@class='page-title']")
        public WebElement myAccountTitleHD;

        @FindBy(xpath = "//span[@class='icon-box-icon icon-account']")
        public WebElement myAccountButtonHD;

        @FindBy(xpath = "//input[@name='account_first_name']")
        public WebElement myAccountFirstNameHD;

        @FindBy(xpath = "//input[@name='account_last_name']")
        public WebElement getMyAccountLastNameHD;

        @FindBy(id = "account_email")
        public WebElement getMyEmailAddressHD;

        @FindBy(xpath = "//button[@name='save_account_details']")
        public WebElement accountDetailsSaveChangesHD;

        @FindBy(xpath = "//a[.='Addresses']")
        public WebElement addressButtonHD;




        @FindBy(xpath = "//div[@role='alert']")
        public WebElement accountDetailsSuccessfullyHD;

        @FindBy(xpath = "//select")
        public WebElement countryDdmHD;

        @FindBy(xpath = "(//input[@class='input-text '])[4]")
        public WebElement streetAddressHD;

        @FindBy(xpath = "//button[@class='btn btn-dark btn-rounded btn-sm']")
        public WebElement saveAddressHD;

        @FindBy(xpath = "//*[@role='alert']")
        public WebElement addressChangedSuccessfullyHD;

        @FindBy(xpath = "(//a[@class='edit btn btn-link btn-primary btn-underline mb-4'])[2]")
        public WebElement shippingAddHD;

        @FindBy(xpath = "//input[@id='shipping_first_name']")
        public WebElement shippingFirstNameHD;

        @FindBy(xpath = "//input[@id='shipping_last_name']")
        public WebElement shippingLastNameHD;

        @FindBy(xpath = "//input[@id='shipping_company']")
        public WebElement shippingCompanyNameHD;

        @FindBy(xpath = "(//select)[1]")
        public WebElement shippingCountryDdmHD;

        @FindBy(xpath = "(//*[@class='input-text '])[4]")
        public WebElement shippingStreetHD;

        @FindBy(xpath = "//input[@name='shipping_city']")
        public WebElement shippingTownCityHD;

        @FindBy(xpath = "(//*[@class='input-text '])[7]")
        public WebElement shippingZipCodeHD;

        @FindBy(xpath = "//select[@id='shipping_state']")
        public WebElement shippingStateHD;

        @FindBy(xpath = "(//*[@type='submit'])[2]")
        public WebElement shippingSaveButtonHD;

        @FindBy(xpath = "//div[@class='woocommerce-message alert alert-simple alert-icon alert-close-top alert-success']")
        public WebElement shippingSaveSuccesfullyHD;
        @FindBy(xpath = "//*[@name='account_display_name']")
        public WebElement myAccountDisplayName;

        @FindBy(xpath = "//*[@id='tinymce']")
        public WebElement myAccountBiograpy;

        @FindBy(xpath = "(//*[@type='password'])[1]")
        public WebElement myAccountCurrentPassword;

        @FindBy(xpath = "(//*[@type='password'])[2]")
        public WebElement myAccountNewPassword;

        @FindBy(xpath = "(//*[@type='password'])[3]")
        public WebElement myAccountConfirmPassword;

        @FindBy(xpath = "//*[@class='page-title-bar']")
        public WebElement hesapdetayDegistisrilidyazisi;

        @FindBy(xpath = "//*[@type='search']")
        public WebElement searchBolumu;

        @FindBy(xpath = "(//*[@data-quantity='1'])[1]")
        public WebElement ilkUrunSepetIkonu;

        @FindBy(xpath = "(//*[@class='w-icon-cart'])[1]")
        public WebElement cardButonu;

        @FindBy(xpath = "//*[text()='View cart']")
        public WebElement viewCartButonu;

        @FindBy(xpath = "//*[@class='quantity-plus w-icon-plus']")
        public WebElement cartIcindekiArtiButonu;

        @FindBy(xpath = "(//*[@value='1'])[1]")
        public WebElement cartQuantity;

        @FindBy(xpath = "//*[@name='update_cart']")
        public WebElement updateCart;

        @FindBy(xpath = "//*[text()='Cart updated.']")
        public WebElement cartUpdateYazisi;

        @FindBy(xpath = "//*[@class='cart-information']")
        public WebElement cartInformation;

        @FindBy(xpath = "//*[@class='checkout-button button alt wc-forward']")
        public WebElement proceedTocheckout ;

        @FindBy(xpath = "//*[@id='payment']")
        public WebElement paymentMethodAlani ;

        @FindBy(xpath = "(//*[@name='payment_method'])[1]")
        public WebElement wireTransferCheckbox ;

        @FindBy(xpath = "(//*[@name='payment_method'])[2]")
        public WebElement payAtTheDoorCheckbox ;

        @FindBy(xpath = "//*[text()='Thank you. Your order has been received.']")
        public WebElement thankYouYazisi ;

        @FindBy(xpath = "//*[@name='clear_cart']")
        public WebElement clearCartButton ;

        @FindBy(xpath = "//*[@class='return-to-shop']")
        public WebElement returnToShop ;


        @FindBy(xpath = "//*[@class='register inline-type']")
        public WebElement registerButton;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/vendor-register/'])[2]")
        public WebElement becomeVendor;
        @FindBy(xpath = "(//input[@type])[3]")
        public WebElement emailVendor;
        @FindBy(xpath = "//*[@class='wcfm-text wcfm_email_verified_input']")
        public WebElement ilkverificationCode;
        @FindBy(xpath = "//*[@class='wcfm-text wcfm_ele ']")
        public WebElement passwordVendor;
        @FindBy(xpath = "(//input)[7]")
        public WebElement confirmpasswordVendor;

        @FindBy(xpath = "(//*[@class='input-text '])[7]")
        public WebElement zipCode;




        @FindBy(xpath = "//*[@href='/delete']")
        public WebElement fakeDelete;
        @FindBy(xpath = "(//*[@href='#copy'])[3]")
        public WebElement fakerCopy;
        @FindBy(xpath = "(//*[@class='ns-plk8s-e-16'])[5]")
        public WebElement fakerReklam;
        @FindBy(xpath = "(//*[@class='glyphicon glyphicon-envelope'])")
        public  WebElement fakermailcode;
        @FindBy(xpath = "(//*[@style='margin: 0 0 16px;'])[2]")
        public WebElement fakerAlloverMail;
        @FindBy(xpath = "(//*[@class='wcfm-text wcfm_email_verified_input'])")
        public WebElement verificationCode;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/'])")
        public WebElement myAccountButton;
        @FindBy(xpath = "(//*[@class='woocommerce-MyAccount-navigation-link woocommerce-MyAccount-navigation-link--wcfm-store-manager'])")
        public WebElement storeManagerButton;
        @FindBy(xpath = "(//*[@class='woocommerce-MyAccount-navigation-link woocommerce-MyAccount-navigation-link--orders'])")
        public WebElement ordersButton;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/downloads/'])")
        public WebElement downloadsButton;
        @FindBy(xpath = "(//*[@class='woocommerce-MyAccount-navigation-link woocommerce-MyAccount-navigation-link--edit-address'])")
        public WebElement addressButton;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/edit-account/'])")
        public WebElement accountDetailsButton;
        @FindBy(xpath = "(//*[@class='woocommerce-MyAccount-navigation-link woocommerce-MyAccount-navigation-link--wishlist'])")
        public WebElement wishListButton;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/support-tickets/'])")
        public WebElement supportTicketsButton;
        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/followings/'])")
        public WebElement followingsButton;
        @FindBy(xpath =  "//*[text()='Log out']")
        public WebElement secondLogoutButton;
        @FindBy(xpath = "(//*[@class='edit btn btn-link btn-primary btn-underline mb-4'])")
        public WebElement editBillingAddress;
        @FindBy(xpath = "(//*[@id='billing_first_name'])" )
        public WebElement firstNameBilling;
        @FindBy(xpath = "//select[@id='billing_country']")
        public WebElement countryNameSelect;
        @FindBy(xpath = "(//*[@class='input-text '])[4]")
        public WebElement streetAddress;
        @FindBy(xpath = "//select[@id='billing_state']")
        public WebElement stateNameSelect;
        @FindBy(xpath = "(//h1)[2]")
        public WebElement welcomeToAlloverSign;
        @FindBy(xpath = "(//*[@class='nav-link active'])")
        public WebElement logoutProof;
        @FindBy(xpath = "(//*[@class='input-text '])[6]")
        public WebElement cityName;

        @FindBy(xpath = "(//*[@class='btn btn-dark btn-rounded btn-sm'])")
        public WebElement saveAddressButton;
        @FindBy(xpath = "(//*[@class='woocommerce-message alert alert-simple alert-icon alert-close-top alert-success'])")
        public WebElement addressChangedMessage;



        @FindBy(xpath = "(//input[@type='password'])[1]")
        public WebElement password;
        @FindBy(xpath = "//*[@href='https://allovercommerce.com/store-manager/products-manage/']")
        public WebElement productsAddNew;
        @FindBy(xpath = "(//*[@class='wcfm_menu_item '])[3]")
        public WebElement products;
        @FindBy(xpath = "(//*[@class='text'])[4]")
        public WebElement products1;
        @FindBy(xpath = "//*[@id='catalog_visibility']")
        public WebElement sku;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[1]")
        public WebElement inventory;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[4]")
        public WebElement shipping;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[5]")
        public WebElement attributes;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[7]")
        public WebElement linked;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[8]")
        public WebElement seo;
        @FindBy(xpath = "(//*[@class='page_collapsible_content_holder'])[10]")
        public WebElement advanced;
        @FindBy(xpath = "//*[@id='sku']")
        public WebElement skuBox;
        @FindBy(xpath = "//input[@id='manage_stock']")
        public WebElement manageStock;
        @FindBy(xpath = "//input[@id='stock_qty']")
        public WebElement stockQty;
        @FindBy(xpath = "//input[@id='sold_individually']")
        public WebElement soldIndividually;
        @FindBy(xpath = "//*[@id='weight']")
        public WebElement weight;
        @FindBy(xpath = "//*[@id='length']")
        public WebElement lenght;
        @FindBy(xpath = "//*[@id='width']")
        public WebElement widht;
        @FindBy(xpath = "//*[@id='height']")
        public WebElement height;
        @FindBy(xpath = "//*[@id='backorders']")
        public WebElement backordersBox;
        @FindBy(xpath = "//*[@id='product_type']")
        public WebElement simpleProductBox;
        @FindBy(xpath = "//*[@id='is_virtual']")
        public WebElement virtualBox;
        @FindBy(xpath = "//*[@id='is_downloadable']")
        public WebElement downloadableBox;
        @FindBy(xpath = "//*[@id='pro_title']")
        public WebElement productTitle;
        @FindBy(xpath = "//*[@id='regular_price']")
        public WebElement priceBox;
        @FindBy(xpath = "//*[@id='sale_price']")
        public WebElement salePriceBox;
        @FindBy(xpath = "//*[@id='_wcfmmp_processing_time']")
        public WebElement processing;
        @FindBy(xpath = "//*[@id='attributes_is_active_1']")
        public WebElement attributesColor;
        @FindBy(xpath = "(//*[@class='select2-selection__rendered'])[2]")
        public WebElement colorbox;
        @FindBy(xpath = "//*[@id='attributes_is_active_2']")
        public WebElement size;
        @FindBy(xpath = "(//*[@class='select2-selection__rendered'])[3]")
        public WebElement sizeBox;

        @FindBy(xpath = "(//*[@class='wcfmfa fa-eye text_tip'])[3]")
        public WebElement viewButton;
        @FindBy(xpath = "//*[@id='wcfm_products_simple_draft_button']")
        public WebElement draftButton;
        @FindBy(xpath = "//*[@id='wcfm_products_manage_form']")
        public WebElement inventoryMenu;
        @FindBy(xpath = "//*[@class='summary entry-summary']")
        public WebElement productDetails;
        @FindBy(xpath = "//*[@href='#tab-additional_information']")
        public WebElement specificationButton;
        @FindBy(xpath = "//*[@class='woocommerce-tabs wc-tabs-wrapper tab tab-nav-simple']")
        public WebElement specificationDetails;
        @FindBy(xpath = "(//*[@class='wcfm_title wcfm_full_ele'])[1]")
        public WebElement categoriesBox;
        @FindBy(xpath = "(//*[@name='product_cats[]'])[1]")
        public WebElement categoriesFirstBox;
        @FindBy(xpath = "//*[@id='wcfm-products_wrapper']")
        public WebElement productDetails1;


        @FindBy (xpath = "//*[@class='icon-box-icon icon-orders']")
        public WebElement orderbutton;

        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/my-account-2/'])")
        public WebElement myAccount;

        @FindBy (xpath = "//*[@class='woocommerce-notices-wrapper']")
        public WebElement selectVerify;

        @FindBy (xpath = "(//h2)[2]")
        public WebElement relatedProduct;

        @FindBy (xpath = "//*[@type='text'][2]")
        public WebElement billingFirstName;

        @FindBy(xpath = "(//*[@href='https://allovercommerce.com/store-manager/'])")
        public WebElement storeManager;

        @FindBy(xpath =  "(//*[@href='https://allovercommerce.com/store-manager/coupons/'])")
        public WebElement coupons;

        @FindBy (xpath = "//*[@class='add_new_wcfm_ele_dashboard text_tip']")
        public WebElement addNewButton;

        @FindBy (xpath = "//*[@type='text']")
        public WebElement code;

        @FindBy(css = "[id=description]")
        public WebElement description;

        @FindBy(css =  "[id=coupon_amount]")
        public WebElement couponAmount;

        @FindBy(xpath = "//select[@id='discount_type']")
        public WebElement discountTypeSelect;

        @FindBy(xpath = "//*[@type='checkbox']")
        public WebElement freeShipping;

        @FindBy(css = "[id=show_on_store]")
        public WebElement showOnStore;

        @FindBy(xpath = "//*[@type='submit']")
        public WebElement submitButton;

        //@FindBy (xpath = "(//h2)[2]")
        // public WebElement editCoupon;

        @FindBy (xpath = "//*[@class= yes-js js_active js']")
        public WebElement couponSuccess;




        @FindBy(xpath = "//*[@name='save_address']")
        public WebElement saveAddress;




        @FindBy(xpath = "//select[@name='billing_country']")
        public WebElement countryName;

        @FindBy(xpath = "(//*[@class='order-total'])")
        public WebElement totalAmount;



        @FindBy(xpath = "(//*[@id='payment_method_cod'])[1]")
        public WebElement payAtTheDoorRadio;
        @FindBy(xpath = "(//*[@id='payment_method_bacs'])[1]")
        public WebElement payWireTransfer;

        @FindBy (xpath = "(//*[@class='woocommerce-orders-table__row woocommerce-orders-table__row--status-on-hold order'])")
        public WebElement orderDetails;



        @FindBy(xpath = "(//*[@class='current'])")
        public WebElement orderComplete;





        @FindBy(xpath = "(//*[@type='search'])[1]")
        public WebElement aramaKutusuMAK;

        @FindBy(xpath = "(//*[@type='submit'])[2]")
        public WebElement addToCartButonuMAK;

        @FindBy(xpath = "(//*[@class='w-icon-cart'])[1]")
        public WebElement cartIkonuMAK;

        @FindBy(xpath = "//*[@href='https://allovercommerce.com/checkout-2/']")
        public WebElement checkoutButonuMAK;

        @FindBy(xpath = "//*[@class='showcoupon']")
        public WebElement enterYourCodeButonuMAK;

        @FindBy(xpath = "//*[@name='coupon_code']")
        public WebElement couponCodeYazmaAlaniMAK;

        @FindBy(xpath = "//*[@name='apply_coupon']")
        public WebElement applyCouponButonuMAK;

        @FindBy(xpath = "//*[@role='alert']")
        public WebElement couponMessageVerifyMAK;

        @FindBy(xpath = "//*[@class='button alt']")
        public WebElement placeOrderButonuMAK;

        @FindBy(xpath = "(//p)[3]")
        public WebElement orderCompleteVerifyMAK;

        @FindBy(xpath = "(//*[@class='input-text '])[1]")
        public WebElement InCheckoutBillingDetailsFirstNameFieldMAK;

        @FindBy(xpath = "(//*[@name='username'])[1]")
        public WebElement usernameAlaniMAK;

        @FindBy(xpath = "//input[@id='coupon_code']")
        public WebElement smCode;



    }















