package project_team09.tests.us06;

import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC03 {

    @Test
    public void test03() {

        //Web sitesine gidilir	https://allovercommerce.com/	Web sitesine erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

        //SigIn butonuna tiklanir 		SigIn Sayfasi erisebilmeli
        HomePage homePage = new HomePage();
        homePage.SignInButton.click();

        //Gecerli bilgiler girilir	"frances.bryken@feerock.com
        //Deneme123Deneme"	"Gecerli bilgiler ile giris yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK"), Keys.TAB,ConfigReader.getProperty("passwordHK"),Keys.ENTER);
        ReusableMethods.bekle(3);

        //"Arama kutusundan istedigi ürünü arama yapilir"	"shoes"	 Arama sonucuna erisebilmeli
        homePage.aramaKutusu.sendKeys("Shoes",Keys.ENTER);

        //Istenilen ürüne tiklanir		tiklanan ürün erisebilmeli
        ReusableMethods.click(homePage.shoesPicture);


        //"""ADD TO CART"" buttonuna tiklanir "ADD TO CART" erisebilmeli
        //"Sayfada Cart buttonuna tiklanir Cart erisebilmeli
        homePage.addToCartButton.click();

        //Ürünü eklendigini test edilir		Ürün eklendigini test edilir
        String eklemeSonucu = homePage.cartEklemeSonucu.getText();
        Assert.assertTrue(eklemeSonucu.contains(" “Shoes” has been added to your cart."));

    }
}
