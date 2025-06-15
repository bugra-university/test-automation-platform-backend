package project_team09.tests.us12;

import com.github.javafaker.Faker;
import project_team09.pages.us12_13_14.AlloverCommercePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TC02_BillingAdresEkleme extends ExtentReport {

    AlloverCommercePage page = new AlloverCommercePage();

    @Test
    public void BillingAdresEkleme() {
        extentTest = ExtentReport.extentReports.createTest("US_12 TC_02 Billing Adres Ekleme ",
                "Vendor billing adres ekleyebilmeli");

        // Kullanıcı adrese gider
        Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrlME"));
        extentTest.info("Kullanıcı adrese gider");

        // Sign a tıklar
        page.signInGirisME.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign a tıklar");

        // Gecerli e mail adresini girer
        page.eMailKutusuME.sendKeys(ConfigReader.getProperty("emailSignInME"));
        extentTest.info("Gecerli e mail adresini girer");

        // sifre girer
        page.passWordKutusuME.sendKeys(ConfigReader.getProperty("emailKutusuSifreME"));
        extentTest.info("sifre girer");

        // sign in butonuna a tıklar
        page.signInButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("sign in butonuna a tıklar");

        // sign out butonuna a tıklar
        page.signOutButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("sign out butonuna a tıklar");

        // Adresses butonuna tıklar.
        Actions actions = new Actions(Driver.getDriver());
        actions.scrollByAmount(500, 500).perform();
        page.addressesTextME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Adresses butonuna tıklar.");

        // Billing Address alanında ADD yazısına tıklar.
        actions.scrollByAmount(500, 500).perform();
        page.addLinkME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Billing Address alanında ADD yazısına tıklar.");

        // Billing address sayfasının açıldığını doğrular
        page.billingAddressSayfasiMe.isDisplayed();
        ReusableMethods.bekle(5);
        extentTest.info("Billing address sayfasının açıldığını doğrular");

        // FirstName-LastName-CompanyName bilgisi girer.
        Faker faker = new Faker();
        ReusableMethods.bekle(2);
        page.firstNameKutuME.clear();
        page.firstNameKutuME.sendKeys(faker.name().firstName());
        ReusableMethods.bekle(2);
        page.lastNameKutuME.clear();
        page.lastNameKutuME.sendKeys(faker.name().lastName());
        ReusableMethods.bekle(1);
        extentTest.info("FirstName-LastName-CompanyName bilgisi girer.");

        // Kullanıcı Country/Region alanını doldurur
        ReusableMethods.ddmVisibleText(page.countryRegionME, "Turkey");
        ReusableMethods.bekle(1);
        extentTest.info("Kullanıcı Country/Region alanını doldurur");

        // Street address alanına adres bilgilerini girer.
        String addres = faker.address().fullAddress();
        page.streetAddressKutuME.clear();
        page.streetAddressKutuME.sendKeys(addres);
        ReusableMethods.bekle(1);
        extentTest.info("Street address alanına adres bilgilerini girer.");

        // Postcode/zip alanına posta kodu girer.
        page.postcodeME.clear();
        page.postcodeME.sendKeys(ConfigReader.getProperty("postcodeME"));
        ReusableMethods.bekle(1);
        extentTest.info("Postcode/zip alanına posta kodu girer.");

        // Town/City bilgisi girer.
        String town = faker.address().city();
        page.townME.clear();
        page.townME.sendKeys(town);
        ReusableMethods.bekle(1);
        extentTest.info("Town/City bilgisi girer.");

        // province bilgisi girer.
        ReusableMethods.ddmVisibleText(page.provinceNameME, "Adana");
        ReusableMethods.bekle(1);
        extentTest.info("province bilgisi girer.");

        // Phone alanına telefon numarası girer.
        page.phoneNumberME.clear();
        page.phoneNumberME.sendKeys(ConfigReader.getProperty("phoneNumberME"));
        ReusableMethods.bekle(1);
        extentTest.info("Phone alanına telefon numarası girer.");

        // mail adress alanında, kayıt olunan e-mail adresinin otomatik olarak geldiğini
        // doğrular.
        ReusableMethods.scroll(page.billingMailKutusuME);
        Assert.assertTrue(
                page.billingMailKutusuME.getAttribute("value").contains(ConfigReader.getProperty("emailSignInME")));
        ReusableMethods.bekle(1);
        extentTest.info("mail adress alanında, kayıt olunan e-mail adresinin otomatik olarak geldiğini doğrular.");

        // Save Addres butonuna tıklar.
        page.saveAddressButtonME.submit();
        ReusableMethods.bekle(1);
        extentTest.info("Save Addres butonuna tıklar.");

        // "Address changed successufly" yazısının görüldüğünü doğrular.
        Assert.assertTrue(page.addressChangedME.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("'Address changed successufly' yazısının görüldüğünü doğrular.");

        // sayfa kapatilir
        Driver.closeDriver();
        extentTest.info("sayfa kapatilir");
    }
}
