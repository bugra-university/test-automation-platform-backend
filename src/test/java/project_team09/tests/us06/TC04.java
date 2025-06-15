package project_team09.tests.us06;

import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC04 {

    @Test
    public void test04() {

        //Web sitesine gidilir	https://allovercommerce.com/	Web sitesine erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

        //SigIn butonuna tiklanir 		SigIn Sayfasi erisebilmeli
        HomePage homePage = new HomePage();
        homePage.SignInButton.click();

        //Gecerli bilgiler girilir	"frances.bryken@feerock.com
        //Deneme123Deneme"	"Gecerli bilgiler ile giris
        //yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK"), Keys.TAB,ConfigReader.getProperty("passwordHK"),Keys.ENTER);
        ReusableMethods.bekle(3);


        //"Arama kutusundan istedigi ürünü
        //arama yapilir"	"shoes"	Arama sonucuna erisebilmeli
        homePage.aramaKutusu.sendKeys("Shoes",Keys.ENTER);


        //Istenilen ürüne tiklanir		tiklanan ürün erisebilmeli
        ReusableMethods.click(homePage.shoesPicture);

        //"""ADD TO CART"" buttonuna tiklanir
        //"		"ADD TO CART" erisebilmeli
        homePage.addToCartButton.click();

        //"Sayfada Cart buttonuna tiklanir
        //"		Cart erisebilmeli
        homePage.cartButton.click();

        //Sayfada view Chart tiklanir		Sayfa erisebilmeli
        homePage.viewCartButton.click();


        //"Sayfada ""+"" isaretine tiklanir ve
        //""upgrade Chart"" tiklanir "		Ürun miktari artildigi test edilir
        ReusableMethods.bekle(3);
        ReusableMethods.click(homePage.plusButton);
        ReusableMethods.bekle(3);
        homePage.updateCartButton.click();
        ReusableMethods.bekle(1);
        String text = homePage.eklemeDogrulama.getText();
        Assert.assertTrue(text.contains("Cart updated."));

        //"Sayfada ""-"" isaretine tiklanir ve
        //""upgrade Chart"" tiklanir "		Ürun miktari azaldigi test edilir
        ReusableMethods.bekle(3);
        homePage.minusButton.click();
        ReusableMethods.bekle(3);
        homePage.updateCartButton.click();
        ReusableMethods.bekle(1);
        String text1 = homePage.eklemeDogrulama.getText();
        Assert.assertTrue(text1.contains("Cart updated."));
    }
}
