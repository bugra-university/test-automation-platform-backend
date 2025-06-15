package project_team09.tests.us05;

import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.meryem.AccountDetailsPageMeryem;
import project_team09.utilities.ExtentReport;

public class Tc01 extends ExtentReport {
    @Test
    public void test01() {
        extentTest =extentReports.createTest("US05","AccountDetailsEdit");
        AccountDetailsPageMeryem page=new AccountDetailsPageMeryem();
        AccountDetailsEditMethodClass. signIn();
        /*
        ilk 6 step bu methodun içinde
         */

        //"Account Details" sayfasının görüldüğünü doğrula
        String expectedBaslik="Account Details";
        String actualBaslik=page.accountDetailsBaslik.getText();
        Assert.assertEquals(expectedBaslik,actualBaslik);
        extentTest.info("Account Details SAYFASININ GORULDUGUNU DOGRULANDI");


    }
}
