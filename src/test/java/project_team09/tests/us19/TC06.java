package project_team09.tests.us19;


import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.Loginpage;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC06 {

    @Test
    public static void test01() {

        MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();
        Loginpage loginpage = new Loginpage();
        Orders orders = new Orders();


        //Anasayfaya gidip sing in olunmali
        Driver.getDriver().get(ConfigReader.getProperty("alloverUrl"));
        myAccountPageSÖ.signIn.click();
        ReusableMethods.bekle(2);

        myAccountPageSÖ.signInUserNameOrEmaill.sendKeys(ConfigReader.getProperty("email"));

        loginpage.password.sendKeys(ConfigReader.getProperty("password"), Keys.ENTER);
        ReusableMethods.bekle(3);

        // myaccount ve adress butonuna tıklanır
        orders.myaccountbutonu.click();
        orders.adressbutonu.click();

        // add,firsname,lastname  butonuna click yap
        orders.ADDbutonu.click();
        orders.firstname.click();
        orders.lastname.click();

        //country/region
        orders.ÜLKEveBÖLGE.click();

        //STREET ADRES , STATE,ZİP CODE, PHONE GİRİLİR
       orders.STREETADRESS.click();
       orders.province.click();
       orders.ZİPKODU.click();
       orders.PHONENUMBER.click();
       orders.EMAİL.click();


        // SAVEADRESS BUTONUNA TIKLANIR
        orders.saveadress.click();






    }
}