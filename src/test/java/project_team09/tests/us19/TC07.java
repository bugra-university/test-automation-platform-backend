package project_team09.tests.us19;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import project_team09.pages.us18_19_20.Loginpage;
import project_team09.pages.us18_19_20.MyAccountPageSÖ;
import project_team09.pages.us18_19_20.Orders;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC07 {


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

        //-cart ve checkout butonuna  click yap
        orders.cart.click();
        orders.checkout.click();

        



        //paymentmetods ve get pay at dor  secilebilmeli

        ReusableMethods.bekle(3);
        Select ddm = new Select(orders.PAYMENTSMETODSWİRWEFT);
        ddm.selectByVisibleText(ConfigReader.getProperty("PAYMENTSMETODS"));

        Select DDM = new Select(orders.getPayAtDoor);
        DDM.selectByVisibleText(ConfigReader.getProperty("getpay at door"));






    }
}





















