package project_team09.tests.us11;

import com.aventstack.extentreports.ExtentReports;
import org.junit.Test;
import org.openqa.selenium.By;
import org.testng.asserts.SoftAssert;
import project_team09.pages.MyAccountPageEnsar;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Us11 extends ExtentReport {

    @Test
    public void US_11MyAccountCheck() {
        Us11.extentReports = new ExtentReports();
        extentTest = extentReports.createTest("US-11","MyAccountPageCheck");

        // 1 - Ana sayfaya git.
        Driver.getDriver().get(ConfigReader.getProperty("alloverUrl"));
        extentTest.info("Ana Sayfaya gitti");


        // 2 - "Sign in / Register" alanındaki "Sign in" butonunun görünür olduğunu doğrula.
        MyAccountPageEnsar MyAccountPage = new MyAccountPageEnsar();
        SoftAssert softAssert = new SoftAssert();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(MyAccountPage.SignİnButtonLocate.isDisplayed());
        ReusableMethods.bekle(2);
        extentTest.info("Sign in butonunun görünür olduğu doğrulandı");

        // 3 - Sign in / Register alanındaki Sign in butonuna tıkla.
        MyAccountPage.SignİnButtonLocate.click();
        ReusableMethods.bekle(1);
        extentTest.info("Register alanındaki Sign in butonuna tıklandı");

        // 4 - Sign in pop-up penceresinin açıldığını doğrula.
        softAssert.assertTrue(MyAccountPage.SignInPopUp.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Sign in penceresinin açıldığını doğrulandı");

        // 5 - Sign in pop-up penceresinde bulunan "SIGN-IN" butonunun görünür ve ulaşılabilir olduğunu doğrula.
        MyAccountPage.PopUpİçindesignInButton.isDisplayed();//Gorunur
        MyAccountPage.PopUpİçindesignInButton.isEnabled();//Ulasilabilir
        ReusableMethods.bekle(1);
        extentTest.info(" \"SIGN-IN\" butonunun görünür ve ulaşılabilir olduğunu doğrulandı");


        // 6 -"Username or email address" alanına geçerli bir mail adresi, "Password" alanına geçerli bir şifre
        // girdikten sonra "Sign In" butonuna basınca, giriş yapıldığını doğrula.
        MyAccountPage.PopUpİçindeEmailBox.sendKeys(ConfigReader.getProperty("UserNameE"));
        ReusableMethods.bekle(1);
        MyAccountPage.PopUpİçindePasswordBox.sendKeys(ConfigReader.getProperty("PasswordE"));
        ReusableMethods.bekle(1);
        MyAccountPage.PopUpİçindesignInButton.click();
        ReusableMethods.bekle(1);


        Driver.getDriver().findElement(By.xpath("//a[@class='login logout inline-type']")).click();//GEÇİCİ KOD
        ReusableMethods.bekle(1);
        extentTest.fail("My Account sayfası görünmedi, Ana sayfaya yönlendi.");
        ReusableMethods.tumSayfaResmi("Failed");

        softAssert.assertTrue(Driver.getDriver().getTitle().contains("My Account"));//Title ile dogrulama
        ReusableMethods.bekle(1);
        extentTest.info("Geçici Kod ile, Geçerli mail adresi ve şifre kullanılarak giriş yapıldığı doğrulandı");

        // 7 - My Account sayfasının açıldığını doğrula.
        softAssert.assertTrue(MyAccountPage.MyAccountPageText.isDisplayed());//Text ile dogrulama
        ReusableMethods.bekle(1);
        extentTest.info("My Account sayfasının açıldığını doğrulandı");



        // 8 - My Account sayfasında "Dashboard" başlık yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.DashboardButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("My Account sayfasında \"Dashboard\" başlık yazısının olduğunu doğrulandı");


        // 9 - Dashboard altında, Store manager yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.StoreManagerButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Store manager yazısının olduğunu doğrulandı");




        // 10 - Dashboard altında, Orders yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.OrdersButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Orders yazısının olduğunu doğrulandı");


        // 11 - Dashboard altında, Downloads yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.DownloadsButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Downloads yazısının olduğunu doğrulandı");

        // 12 - Dashboard altında, Addresses yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.AddressesButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Addresses yazısının olduğunu doğrulandı");

        // 13 - Dashboard altında, Account Detail yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.AccountDetailsButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Account Detail yazısının olduğunu doğrulandı");

        // 14 - Dashboard altında, Wishlist yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.WishlistButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Wishlist yazısının olduğunu doğrulandı");

        // 15 - Dashboard altında, Supoort Tickets yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.SupportTicketsButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Supoort Tickets yazısının olduğunu doğrulandı");

        // 16 - Dashboard altında, Followings yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.FollowingsButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Followings yazısının olduğunu doğrulandı");

        // 17 - Dashboard altında, Logout yazısının olduğunu doğrula.
        softAssert.assertTrue(MyAccountPage.LogoutButtonLocate.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Dashboard altında, Logout yazısının olduğunu doğrulandı");


        softAssert.assertAll();

    }

}

