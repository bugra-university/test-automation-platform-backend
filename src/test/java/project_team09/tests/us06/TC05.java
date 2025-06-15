package project_team09.tests.us06;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us06_us07.HomePage;
import project_team09.pages.us06_us07.MyAccount;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC05 {


    @Test
    public void test05() {



        //Web sitesine gidilir	https://allovercommerce.com/	Web sitesine erişilebilmeli.
        Driver.getDriver().get(ConfigReader.getProperty("allovercommerceURLHK"));

        //SigIn butonuna tiklanir 		SigIn Sayfasi erisebilmeli
        HomePage homePage = new HomePage();
        homePage.SignInButton.click();

        //Gecerli bilgiler girilir	"frances.bryken@feerock.com
        //Deneme123Deneme"	"Gecerli bilgiler ile giris
        //yapilabilmeli"
        homePage.Username.sendKeys(ConfigReader.getProperty("usernameHK2"), Keys.TAB,ConfigReader.getProperty("passwordHK2"),Keys.ENTER);
        ReusableMethods.bekle(10);

        //Sayfada My Account tiklanir		My Account erisebilmeli
        MyAccount myAccount = new MyAccount();
        ReusableMethods.scroll(myAccount.myAccountButton);
        ReusableMethods.bekle(2);
        myAccount.myAccountButton.click();

        //Sayfada Adresses tiklanir		Adresses Sayfasi erisebilmeli
        myAccount.adressesButton.click();

        //Sayfada Add butonu tiklanir		Add butonu erisebilmeli
        Actions actions = new Actions(Driver.getDriver());
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(3);
        myAccount.addButton.click();

        //FirstName girilir	John	FirstName girilebilmeli
        //LastName girilir	Max	LastName girilebilmeli
        //Country/Region	United States (US)	Country/Region secilebilmeli
        ReusableMethods.bekle(3);
        myAccount.textBox.sendKeys("John",Keys.TAB,"Max",Keys.TAB,Keys.TAB,Keys.ENTER,"United States (US)",Keys.ENTER);


        //Street address girilir	Down Street 10	Street addres girilebilmeli
        //Town/City girilir	Texas	Town/City girilebilmeli
        //State secilir	Texas	State secilebilmeli
        myAccount.textStreet.sendKeys("Down Street 10",Keys.TAB,Keys.TAB,"Texas",Keys.TAB,Keys.ENTER,"Texas",Keys.ENTER);


        //ZIP Code girilir	88595	ZIP Code girilebilmeli
        //Phone girilir	123456789	Phone girilebilmeli
        myAccount.textZIP.sendKeys("88595",Keys.TAB,"123456789",Keys.TAB,Keys.TAB,Keys.ENTER);


        //Save address butonu tiklanir		"""Address changed successfully."" mesaji alindigi
        //test edilir"
        String dogrulamaMesaj = myAccount.dogrulama.getText();
        Assert.assertTrue(dogrulamaMesaj.contains("Address changed successfully."));

    }
}
