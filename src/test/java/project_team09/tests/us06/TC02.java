package project_team09.tests.us06;


import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC02 {
    @Test
    public void test02() {


    //Web sitesine gidilir	https://allovercommerce.com/	Web sitesine erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

    //SigIn butonuna tiklanir 		SigIn Sayfasi erisebilmeli
        HomePage homePage = new HomePage();
        homePage.SignInButton.click();

    //Gecerli bilgiler girilir	"frances.bryken@feerock.com
    //Deneme123Deneme"	"Gecerli bilgiler ile giris yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK"), Keys.TAB,ConfigReader.getProperty("passwordHK"),Keys.ENTER);
        ReusableMethods.bekle(3);

    //"Arama kutusundan istedigi ürünü arama yapilir"	"house"	"Arama sonucu ""No products were found matching your selection."" mesaji almali"
        homePage.aramaKutusu.sendKeys("house",Keys.ENTER);
        Assert.assertTrue(homePage.NoaramaSonucu.getText().contains("No products were found matching your selection."));


}
}

