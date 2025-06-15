package project_team09.tests.us18;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.Loginpage;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC11 {


        @Test
        public void test1() {

            Loginpage loginpage = new Loginpage();

            MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();

            Orders orders =new Orders();

            //Anasayfaya gidip sing in olunmali

            Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrlsö"));

            loginpage.singIn.click();

            ReusableMethods.bekle(3);

            loginpage.userName.sendKeys(ConfigReader.getProperty("email"));

            loginpage.password.sendKeys(ConfigReader.getProperty("password"), Keys.ENTER);

            ReusableMethods.bekle(3);

            myAccountPageSÖ.signIn.click();

            loginpage.myAccountYazisi.click();

            loginpage.storeManagerText.click();



            //kupona click yap
            //Code yazabilmeliyim
            //Description yazılabilmeli
            Actions action = new Actions(Driver.getDriver());

            action.sendKeys(Keys.PAGE_DOWN,
                    Keys.PAGE_DOWN,
                    Keys.PAGE_DOWN,
                    Keys.PAGE_DOWN,
                    Keys.PAGE_DOWN).perform();
            ReusableMethods.bekle(3);

            // my acccount ve store manager click yap
            orders.myAccountSayfaEnAlt.click();

            orders.storeManager.click();
            ReusableMethods.bekle(5);
            action.sendKeys(Keys.PAGE_DOWN).perform();
            orders.coupons.click();
            ReusableMethods.bekle(5);
            orders.addNewCoupon.click();
            ReusableMethods.bekle(5);

            //desciription kısmına kuponla ilgili description yaz

            JavascriptExecutor js= (JavascriptExecutor) Driver.getDriver();
            js.executeScript("arguments[0].value='Tek9638'",orders.code);
            //   js.executeScript("arguments[0].value='Teknolojik Aletler Icin Gecerlidir.'",orders.couponDescription);

            //Discount Type; Percentage discount veya Fixed Product Discount olarak seçilebilmeli
            ReusableMethods.bekle(5);
            Select ddm = new Select(orders.discontType);
            ddm.selectByVisibleText(ConfigReader.getProperty("couponDiscontType"));

            //Coupon Amount yazılabilmeli
            //Coupon expiry date girilebilmeli
            ReusableMethods.bekle(5);
            js.executeScript("arguments[0].value='20'",orders.couponAmount);
            js.executeScript("arguments[0].value='2023.08.01'",orders.couponExpiryDate);
            action.sendKeys(Keys.PAGE_DOWN).perform();

            //Allow free shipping, Show on store seçilebilmeli
            ReusableMethods.bekle(5);
            js.executeScript("arguments[0].click();",orders.couponChechBox1);
            js.executeScript("arguments[0].click();",orders.couponChechBox2);
            ReusableMethods.bekle(5);

            // submit butonuna tıkla

            js.executeScript("arguments[0].click();",orders.couponSubmit);
            ReusableMethods.bekle(5);
            orders.coupons.click();
            ReusableMethods.bekle(5);

            //Coupons oluşturulduğu görülmeli
            Assert.assertTrue(orders.couponDescription.isDisplayed());
        }

    }












