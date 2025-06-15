package project_team09.tests.us10;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import project_team09.pages.MyAccountPageEnsar;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Us10 extends ExtentReport {

    @DataProvider
    public static Object[][] Password() {
        return new Object[][]{
                {ConfigReader.getProperty("tooshort"),
                        ConfigReader.getProperty("weak"),
                        ConfigReader.getProperty("good"),
                        ConfigReader.getProperty("strong")},

                {ConfigReader.getProperty("tooshort2"),
                        ConfigReader.getProperty("weak2"),
                        ConfigReader.getProperty("good2"),
                        ConfigReader.getProperty("strong2")}

        };
    /*
    Password seviyeleri görülebilmeli ('Become a Vendor' Kaydı için)
    -too short
    - weak
    - good
    - strong
    */

    }

    @Test(dataProvider = "Password")
    public void test01(String tooshort, String weak, String good, String strong) {

        ReusableMethods.vendorKayit();
        extentTest = ExtentReport.extentReports.createTest("Vendor Kaydı US-10 TC-01", "Password seviyeleri görülebilmeli (Vendor Kaydı için)");
        ReusableMethods.bekle(5);

        extentTest.info("Anasayfaya gidildi.");
        extentTest.info("Tum sayfa resmi alındı");
        extentTest.info("Register butonuna tiklandı");
        extentTest.pass("Çıkan ekranda 'Become a Vendor' yazısının göründüğü doğrulandı.");
        extentTest.info("Become a Vendor butonuna tıklandı.");
        extentTest.pass("Vendor Registration sayfasının göründüğü doğrulandı");

        //Password alanina 6 karakterden az bir password gir.
        MyAccountPageEnsar MyAccountPage = new MyAccountPageEnsar();
        MyAccountPage.PasswordTextBoxLocate.sendKeys(tooshort);
        ReusableMethods.bekle(5);
        extentTest.info("Password kutusuna 6 karakterden az bir password girildi.");


        //"Too short" mesajının göründüğünü doğrula.
        Assert.assertEquals(ConfigReader.getProperty("short"), MyAccountPage.verifyPassword.getText());
        ReusableMethods.bekle(5);
        extentTest.info("Too short mesajının göründüğü doğrulandı.");
        ReusableMethods.webElementResmi(MyAccountPage.verifyPassword);
        extentTest.info("Webelement resmi alındı.(Too short)");
        MyAccountPage.PasswordTextBoxLocate.clear();
        extentTest.info("Passwordu alanındaki password silindi.");


        //"Password alanina 6 karakterli sadece rakam veya küçük harf veya büyük harf veya special karakter içeren bir password gir.
        //(4 kriter aynı anda kullanılmayacak)"

        MyAccountPage.PasswordTextBoxLocate.sendKeys(weak);
        extentTest.info("Password alanina 6 karakterli sadece rakam veya küçük harf veya büyük harf veya special karakter içeren bir password girildi (Büyük Harf)");


        //"Weak" mesajının göründüğünü doğrula.
        Assert.assertTrue(MyAccountPage.verifyPassword.getText().contains("Weak"));
        ReusableMethods.bekle(2);
        extentTest.pass("Weak mesajının göründüğü doğrulandı.");
        ReusableMethods.webElementResmi(MyAccountPage.verifyPassword);
        ReusableMethods.bekle(2);
        extentTest.info("WebElement resmi alındı.(Weak)");

        //Passwordu alanındaki passwordu sil.
        MyAccountPage.PasswordTextBoxLocate.clear();
        ReusableMethods.bekle(2);

        extentTest.info("Passwordu alanındaki password silindi.");

        //Password 6 karakterli en az bir büyük harf, küçük harf, rakam ve special karakter içeren bir password gir.

        MyAccountPage.PasswordTextBoxLocate.sendKeys(good);
        ReusableMethods.bekle(2);
        extentTest.info("Password 6 karakterli en az bir büyük harf, küçük harf, rakam ve special karakter içeren bir password girildi.");
        MyAccountPage.PasswordTextBoxLocate.clear();

        //"Good" mesajının göründüğünü doğrula.

        Assert.assertEquals("Good", MyAccountPage.verifyPassword.getText());
        ReusableMethods.bekle(2);
        extentTest.pass("Good mesajının göründüğü doğrulandı.");
        ReusableMethods.webElementResmi(MyAccountPage.verifyPassword);
        ReusableMethods.bekle(2);
        extentTest.info("Webelement resmi alındı.(Good)");


        //Passwordu alanındaki passwordu sil.

        MyAccountPage.PasswordTextBoxLocate.clear();
        extentTest.info("Passwordu alanındaki password silindi.");

        //Password sevitesinde Good yazacak şekilde password gir.

        MyAccountPage.PasswordTextBoxLocate.sendKeys(ConfigReader.getProperty("good2"));
        extentTest.info("Password 6 karakterli en az bir büyük harf, küçük harf, rakam içeren bir password girildi.");

        //Password 6 karakterli en az bir büyük harf ,küçük harf ve sayı içeren ve (*,?,&,%,!,#,$,^,',',_) karakterlerinden en az birini içeren bir password gir.

        MyAccountPage.PasswordTextBoxLocate.sendKeys(strong);
        extentTest.info("Password 6 karakterli en az bir büyük harf ,küçük harf ve sayı içeren ve (*,?,&,%,!,#,$,^,',',_) karakterlerinden en az birini içeren bir password girildi.");

        //"Strong" mesajının göründüğünü doğrula.

        Assert.assertEquals("Strong", MyAccountPage.verifyPassword.getText());
        extentTest.pass("Strong mesajının göründüğü doğrulandı.");
        ReusableMethods.webElementResmi(MyAccountPage.verifyPassword);
        extentTest.info("Webelement resmi alındı.(Strong)");
        /*
         Son kodlarlabirlikte User Story 10 u tamamlamış oluyoruz
         -too short
         - weak
         - good
         - strong
         Password seviyelerinin hepsini yerine yazıp doğrulamasını yapıyoruz
         bu sırada da hepsinin ekran fotoğraflarını alarak kodlarımızı da
         görüntülerle desteklemiş oluyoruz.

         */
    }
}


