package project_team09.tests.us06;

import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.BuyPage;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC06 {

    @Test
    public void test06() {

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

        //"Sayfada Payment Methods altinda
        //""Wire transfer/EFT"" erisebilmeli"		"Wire transfer/EFT" görüldügü test edilebilmeli
        Assert.assertTrue(buyPage.eftYazisi.isDisplayed());


        //"Sayfada Payment Methods altinda
        //""Pay at the door"" erisebilmeli"		"Pay at the door" görüldügü test edilebilmeli
        Assert.assertTrue(buyPage.payYazisi.isDisplayed());

        //"Sayfada Payment Methods altinda
        //""Wire transfer/EFT"" ve
        //""Pay at the door"" secilebilmeli"		Secildigi test edilebilmeli
        Assert.assertTrue(buyPage.eftSelect.isSelected());
        ReusableMethods.click(buyPage.paySelect);
        Assert.assertTrue(buyPage.paySelect.isSelected());

    }
}
