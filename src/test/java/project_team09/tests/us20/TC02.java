package project_team09.tests.us20;

import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.Loginpage;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;

public class TC02 {
    @Test
    public void test01() {


        //1-Go to allovercommerce site
        Driver.getDriver().get(ConfigReader.getProperty("https://allovercommerce.com/"));


        Orders orders = new Orders();
        MyAccountPageSÖ myAccountPageSÖ = new MyAccountPageSÖ();
        Loginpage loginpage = new Loginpage();


        myAccountPageSÖ.signIn.click();

        myAccountPageSÖ.signInUserNameOrEmaill.sendKeys(Keys.ENTER);

        loginpage.password.click();
        loginpage.singIn.click();

        orders.myaccountbutonu.click();




        



    }
}
