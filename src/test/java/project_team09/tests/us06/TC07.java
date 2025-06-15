package project_team09.tests.us06;

import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.BuyPage;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC07 {

    @Test
    public void test07() {

        HomePage homePage = new HomePage();
        BuyPage buyPage = new BuyPage();

        //Web sitesine gidilir	https://allovercommerce.com/	Web sitesine erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

        //SigIn butonuna tiklanir 		SigIn Sayfasi erisebilmeli
        homePage.SignInButton.click();

        //Gecerli bilgiler girilir	"frances.bryken@feerock.com
        //Deneme123Deneme"	"Gecerli bilgiler ile giris
        //yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK"), Keys.TAB,ConfigReader.getProperty("passwordHK"),Keys.ENTER);

        //Cart buttonuna tiklanir		Sayfa erisebilmeli
        ReusableMethods.bekle(2);
        homePage.cartButton.click();

        //Sayfada Checkout tiklanir		Sayfa erisebilmeli
        homePage.checkoutButton.click();

        //Sayfada Place Order tiklanir		Place Order erisebilmeli
        ReusableMethods.click(buyPage.placeOrder);

        //"Sayfada ""Thank you.
        //Your order has been received.""
        //mesaji erisebilmeli"		"""Thank you. Your order has been received.""
        //mesaji alindigini test edilir"
        Assert.assertTrue(buyPage.buyDogrulama.getText().contains("Thank you. Your order has been received."));
    }
}
