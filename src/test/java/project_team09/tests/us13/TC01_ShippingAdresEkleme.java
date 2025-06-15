package project_team09.tests.us13;

import com.github.javafaker.Faker;
import project_team09.pages.us12_13_14.AlloverCommercePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;


public class TC01_ShippingAdresEkleme extends ExtentReport {

    AlloverCommercePage page = new AlloverCommercePage();
    Actions actions = new Actions(Driver.getDriver());

    @Test
    public void BillingAdresEkleme() {
        extentTest = ExtentReport.extentReports.createTest
                ("US_13 TC_01 Shipping Adres Ekleme", "Vendor shipping adres ekleyebilmeli");
        //Kullanıcı adrese gider
        Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrlME"));
        extentTest.info("https://allovercommerce.com/ adresine gidilir");

        //Sign a tıklar
        page.signInGirisME.click();
        ReusableMethods.bekle(2);
        extentTest.info("sign in'e  tiklar");

        //Gecerli e mail adresini girer
        page.eMailKutusuME.sendKeys(ConfigReader.getProperty("emailSignInME"));
        extentTest.info("Gecerli e mail adresini girer");

        //sifre girer
        page.passWordKutusuME.sendKeys(ConfigReader.getProperty("emailKutusuSifreME"));
        extentTest.info("sifre girer");

        //sign in butonuna a tıklar
        page.signInButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("sign in butonuna a tıklar");

        //sign out butonuna a tıklar
        page.signOutButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("SignOut butonuna tıklar");

        //Adresses butonuna tıklar.
        actions.scrollByAmount(500, 500).perform();
        page.addressesTextME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Adresses butonuna tıklar.");

        //Kullanıcı Shipping Address altındaki ADD butonuna tıklar
        actions.scrollByAmount(500, 500).perform();
        page.shippingAdressADDButonuME.click();
        extentTest.info("Kullanıcı Shipping Address altındaki ADD butonuna tıklar");

        //Kullanıcı First name alanını doldurur
        Faker faker = new Faker();
        page.shippingfirstNameME.clear();
        page.shippingfirstNameME.sendKeys(faker.name().firstName());
        extentTest.info("Kullanıcı First name alanını doldurur");

        //Kullanıcı Last name alanını doldurur
        page.shippinglastNameME.clear();
        page.shippinglastNameME.sendKeys(faker.name().lastName());
        extentTest.info("Kullanıcı Last name alanını doldurur");

        //Kullanıcı Country/Region alanını doldurur
        ReusableMethods.ddmVisibleText(page.countryRegionSME, "Turkey");
        extentTest.info("Kullanıcı Country/Region alanını doldurur");
        ReusableMethods.bekle(1);

        //Kullanıcı Street address alanını doldurur
        page.streetadressME.clear();
        page.streetadressME.sendKeys(faker.address().fullAddress());
        extentTest.info("Kullanıcı Street address alanını doldurur");

        //Kullanıcı ZIP Code alanını doldurur
        page.postCodeZipME.clear();
        page.postCodeZipME.sendKeys(ConfigReader.getProperty("postcodeME"));
        extentTest.info("Kullanıcı ZIP Code alanını doldurur");

        //Kullanıcı Town/City alanını doldurur
        page.townCityME.clear();
        page.townCityME.sendKeys(faker.address().city());
        extentTest.info("Kullanıcı Town/City alanını doldurur");

        //Kullanıcı Province Kutusunu tıklar
        ReusableMethods.ddmVisibleText(page.provinceShipME, "İzmir");
        ReusableMethods.bekle(1);
        actions.scrollByAmount(500, 500).perform();
        extentTest.info("Kullanıcı Province Kutusunu tıklar");

        //Kullanıcı save adress butonuna tıklar
        page.saveAddressButtonME.click();
        extentTest.info("Kullanıcı save adress butonuna tıklar");

        //Yeni shipping adress'inin kayıt edildiği resmi alinir
        ReusableMethods.tumSayfaResmi("Shipping Adres Kayıt");
        extentTest.info("Yeni shipping adress'inin kayıt edildiği resmi alinir");

        //sayfa kapatilir
        Driver.closeDriver();
        extentTest.info( "sayfa kapatilir");
    }
}