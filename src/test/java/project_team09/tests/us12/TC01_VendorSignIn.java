package project_team09.tests.us12;


import project_team09.pages.us12_13_14.AlloverCommercePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TC01_VendorSignIn extends ExtentReport {

    AlloverCommercePage page = new AlloverCommercePage();


    @Test
    public void TC_01_VendorSignIn() {

        extentTest = ExtentReport.extentReports.createTest
                ("US_12 TC_01 Vendor Giris ", "Vendor olarak giris yapilabilmeli");
        //Kullanıcı adrese gider
        Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrlME"));
        extentTest.info("https://allovercommerce.com/ adresine gidilir");

        //sign in a tıklar
        page.signInGirisME.click();
        extentTest.info("sign in'e  tiklar");

        //Gecerli e mail adresini girer
        page.eMailKutusuME.sendKeys(ConfigReader.getProperty("emailSignInME"));
        extentTest.info("Gecerli e mail adresini girer");

        //sifre gir
        page.passWordKutusuME.sendKeys(ConfigReader.getProperty("emailKutusuSifreME"));
        extentTest.info("sifre girer");

        //sign in butonuna a tıklar
        page.signInButtonME.click();
        extentTest.info("sign in butonuna a tıklar");

        //SignOut butonuna tıklar
        page.signOutButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("SignOut butonuna tıklar");

        //Vendor olarak giris yaptigini dogrular
        Assert.assertTrue(page.assertME.isDisplayed());
        extentTest.info("Vendor olarak giris yaptigini dogrular");

        //sayfa kapatilir
        Driver.closeDriver();
        extentTest.info("sayfa kapatilir");
    }
}
