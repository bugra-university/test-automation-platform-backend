package project_team09.tests.us07;

import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.ComparePage;
import project_team09.pages.us06_us07.HomePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC01 {

    @Test
    public void test01() {
        HomePage homePage = new HomePage();
        ComparePage comparePage = new ComparePage();

        // Web sitesine gidilir https://allovercommerce.com/ Web sitesine
        // erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

        // SigIn butonuna tiklanir SigIn Sayfasi erisebilmeli
        homePage.SignInButton.click();

        // Gecerli bilgiler girilir "frances.bryken@feerock.com
        // Deneme123Deneme" "Gecerli bilgiler ile giris
        // yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK"), Keys.TAB,
                ConfigReader.getProperty("passwordHK"), Keys.ENTER);

        // "Arama kutusundan istedigi ürünü
        // arama yapilir" "Shoes" Sayfa erisebilmeli
        ReusableMethods.bekle(3);
        homePage.aramaKutusu.sendKeys("shoes", Keys.ENTER);

        // Sayfada istenilen Ürün tiklanir Ürün tiklanabilmeli
        ReusableMethods.click(homePage.shoesPicture);

        // Sayfada "Compare" buttonuna tiklanir Compare tiklanabilmeli
        comparePage.compareButton.click();

        // "Arama kutusundan istedigi ürünü
        // arama yapilir" "shirt" Sayfa erisebilmeli
        homePage.aramaKutusu.sendKeys("shirt", Keys.ENTER);

        // Sayfada istenilen Ürün tiklanir Ürün tiklanabilmeli
        ReusableMethods.bekle(2);
        ReusableMethods.click(homePage.shirtPicture);

        // Sayfada "Compare" buttonuna tiklanir Compare tiklanabilmeli
        comparePage.compareButton.click();

        // "Arama kutusundan istedigi ürünü
        // arama yapilir" "bag" Sayfa erisebilmeli
        homePage.aramaKutusu.sendKeys("bag", Keys.ENTER);

        // Sayfada istenilen Ürün tiklanir Ürün tiklanabilmeli
        ReusableMethods.bekle(2);
        ReusableMethods.click(homePage.bagPicture);

        // Sayfada "Compare" buttonuna tiklanir Compare tiklanabilmeli
        comparePage.compareButton.click();

        // "Arama kutusundan istedigi ürünü
        // arama yapilir" "watch" Sayfa erisebilmeli
        homePage.aramaKutusu.sendKeys("watch", Keys.ENTER);

        // Sayfada istenilen Ürün tiklanir Ürün tiklanabilmeli
        ReusableMethods.bekle(2);
        ReusableMethods.click(homePage.watchPicture);

        // Sayfada "Compare" buttonuna tiklanir Compare tiklanabilmeli
        comparePage.compareButton.click();

        // Sayfada "Start Compare" tiklanir Sayfa erisebilmeli
        ReusableMethods.click(comparePage.startCompare);

        // "Compare sayfasinda oldugunu kontrol
        // edilir" Compare sayfasinda oldugunu test edilir
        Assert.assertTrue(comparePage.compareYazisi.getText().contains("Compare"));

    }
}
