package project_team09.tests.us03;

import com.github.javafaker.Faker;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import project_team09.pages.Us01Us02Us03.Anasayfa;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class Us03_BillingAdressEkle {


    @Test
    public void tc01_EmailOtomatikGelmeli () {


        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());

        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());

        //sign outa tıkla ve doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"E-mail" kutusunun dolu geldigini dogrula
        softAssert.assertTrue(anasayfa.emailValue.isDisplayed(),"email adresi otomatik geliyor");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();

    }

    @Test
    public void tc02_withoutFirstNameNotBilling() {


        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());

        //"First name" kutusunu bos birak



        //"Last name" sec, "Country/Region"  içine bilgi gir bilgisi gir
        anasayfa.adressesLastNameAs.sendKeys("a",Keys.TAB,  Keys.TAB,"Albania",Keys.TAB);




        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);


        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("isik");
        ReusableMethods.bekle(1);

        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(1);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);
        actions.sendKeys(Keys.PAGE_DOWN).perform();


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.sendKeys("123");
        ReusableMethods.bekle(1);

        //"Phone" bilgisi gir
        anasayfa.phoneAs.sendKeys("05321456622");
        ReusableMethods.bekle(2);



        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);

        //kayıt yapılamadığını dogrula

        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),"siteye eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //driver ı kapat
        Driver.closeDriver();


    }


    @Test
    public void tc03_WithoutLastNameNotBilling() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name" //"Last name" bos birak
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as");


        actions.sendKeys(Keys.PAGE_DOWN).perform();
        //"Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.countryRegionAs.click();
        ReusableMethods.bekle(3);
        anasayfa.countryregionTextAs.sendKeys("Albania",Keys.TAB);
        ReusableMethods.bekle(1);

        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);


        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("isik");
        ReusableMethods.bekle(1);

        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(1);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);
        actions.sendKeys(Keys.PAGE_DOWN).perform();


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.sendKeys("123");
        ReusableMethods.bekle(1);

        //"Phone" bilgisi gir
        anasayfa.phoneAs.sendKeys("05321456622");
        ReusableMethods.bekle(1);

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);

        //kayıt yapılamadığını doğrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),"siteye eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();
    }

    @Test
    public void tc04_WithoutCountryRegionNotBilling() {



        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail2"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name" bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as");

        //"Last name" bilgisi gir
        anasayfa.adressesLastNameAs.sendKeys("a");



        actions.sendKeys(Keys.PAGE_DOWN).perform();

        //"Country/Region" secimi yapma



        actions.sendKeys(Keys.PAGE_DOWN).perform();
        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("isik");
        ReusableMethods.bekle(1);

        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(1);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Alaska",Keys.TAB);
        ReusableMethods.bekle(1);
        actions.sendKeys(Keys.PAGE_DOWN).perform();


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.sendKeys("123");
        ReusableMethods.bekle(1);

        //"Phone" bilgisi gir
        anasayfa.phoneAs.sendKeys("05321456622");
        ReusableMethods.bekle(1);


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");

        //"Phone" bilgisi gir
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622");

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);


        //kayıt yapılamadığını dogrula

        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(),"siteye eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();
        ReusableMethods.bekle(2);

        //Sayfayı kapat
        Driver.closeDriver();


    }


    @Test
    public void tc05_WithoutStreetAdressNotBilling() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"al",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);



        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);


        //"Street Address" bilgisi girme


        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(2);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);



        //"Postcode/ZIP" bilgisi gir

        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");

        //"Phone" bilgisi gir
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622");

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);


        //kayıt yapılaamdığını dogrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(), "eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();



    }

    @Test
    public void tc06_withoutTownCityNotBilling() {

        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"a",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);


        actions.sendKeys(Keys.PAGE_DOWN).perform();


        ReusableMethods.bekle(2);


        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("isik");
        ReusableMethods.bekle(1);

        //Town/City" bilgilerini girME



        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");
        ReusableMethods.bekle(1);

        //"Phone" bilgisi gir
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622");

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);


        //kayıt yapılamadığını dogrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(), "eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();



    }

    @Test
    public void tc07_withoutStateCountyNotBilling() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());


        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"a",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);



        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);


        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("vatan");



        //county/state bilgisini boş bırak


        //Town/City" bilgilerini gir
        anasayfa.townCityAs.click();
        anasayfa.townCityAs.sendKeys("porto");


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");

        //"Phone" bilgisi gir
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622");

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);
        ReusableMethods.bekle(2);


        //kayıt yapılaamdığını dogrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(), "eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();



    }


    @Test
    public void tc08_withoutPostcodeZIPNotBilling() {


        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());

        //Web sitesine git
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"a",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);


        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("porto");


        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(2);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);
        actions.sendKeys(Keys.PAGE_DOWN).perform();


        //"Postcode/ZIP" bilgisi girme


        //"Phone" bilgisi gir //"SAVE ADDRESS" butonuna tikla
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622",Keys.TAB,Keys.TAB,Keys.ENTER);
        ReusableMethods.bekle(2);



        //kayıt yapılaamdığını dogrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(), "eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();


    }


    @Test
    public void tc09_WithoutPhoneNotBilling() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());

        //Web sitesine git
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"a",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);

        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(1);
        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("porto");


        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(2);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");

        //"Phone" bilgisi girme


        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);


        //kayıt yapılaamdığını dogrula
        softAssert.assertTrue(anasayfa.fieldErrorAs.isDisplayed(), "eksik bilgiler ile kayıt yapılamadı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();

    }


    @Test
    public void tc10_eksiksizbilgiIleKayit() {

        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());
        Faker faker = new Faker();

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());


        //email kutusuna kayıtlı olmayan bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());

        //Şifre kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();

        ReusableMethods.bekle(2);


        //Kayıt işleminin gerçekleştiğini doğrula

        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());


        //"First name","Last name","Country/Region" sec içine bilgi gir bilgisi gir
        anasayfa.adressesFirstNameAs.click();
        anasayfa.adressesFirstNameAs.sendKeys("as",Keys.TAB,"al",Keys.TAB,Keys.TAB,"Albania",Keys.TAB);



        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);

        //"Street Address" bilgisi gir
        anasayfa.streetAdressAs.sendKeys("isik");
        ReusableMethods.bekle(1);

        //Town/City" bilgilerini gir
        anasayfa.townCityAs.sendKeys("buyuk");
        ReusableMethods.bekle(1);

        //"state/county" bilgilerini gir
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(1);


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.sendKeys("123");
        ReusableMethods.bekle(1);

        //"Phone" bilgisi gir
        anasayfa.phoneAs.sendKeys("05321456622");
        ReusableMethods.bekle(1);


        //"Postcode/ZIP" bilgisi gir
        anasayfa.postcode_zipAs.click();
        anasayfa.postcode_zipAs.sendKeys("06210");

        //"Phone" bilgisi gir
        anasayfa.phoneAs.click();
        anasayfa.phoneAs.sendKeys("05321456622");

        //"SAVE ADDRESS" butonuna tikla
        anasayfa.saveAdressButonAs.click();
        ReusableMethods.bekle(3);


        //"Adresses" a tıkla ve kayıt yapıldığını dogrula

        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.editBillingButonAs.isDisplayed());
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();


    }

    @Test
    public void tc11_guncellemedeFirstNameLastNameEmailgor() {

        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();
        Actions actions = new Actions(Driver.getDriver());

        //Web sitesine git
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail4"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());

        actions.sendKeys(Keys.PAGE_DOWN).perform();
        //"Billing Address" altindaki "EDIT YOUR BILLING ADDRESS" butonuna tikla
        anasayfa.editBillingButonAs.click();
        ReusableMethods.bekle(3);


        //"First name" kutusunun dolu geldigini dogrula
        softAssert.assertTrue(anasayfa.firstNameEditBilling.isDisplayed(),"first name kutusu dolu geldi");

        //"Last name" kutusunun dolu geldigini dogrula
        softAssert.assertTrue(anasayfa.lastnameEditBilling.isDisplayed(),"last name kutusu dolu geldi");

        //"E-mail" kutusunun dolu geldigini dogrula
        softAssert.assertTrue(anasayfa.emailValue2.isDisplayed(),"email adresi dolu geliyor");
        softAssert.assertAll();


        //Sayfayı kapat
        Driver.closeDriver();





    }


   /* public void denemeTestleri() {
        Anasayfa anasayfa = new Anasayfa();
        SoftAssert softAssert = new SoftAssert();

        //Web sitesine git
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());


        //Gecerli bilgilerle giris yap ve girişi doğrula
        anasayfa.signInAs.click();
        anasayfa.signInEmail.sendKeys(ConfigReader.getProperty("signInEmail1"), Keys.TAB, ConfigReader.getProperty("signInPassword1"), Keys.ENTER);
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());


        //sign outa tıkla ve açıldığını doğrula
        anasayfa.signOutAs.click();
        softAssert.assertTrue(anasayfa.myAccountYazisiAs.isDisplayed());


        //"Adresses" a tıkla ve açıldığını doğrula
        anasayfa.adressesAs.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.adressesYazi.isDisplayed());


        //"Billing Address" altindaki "ADD" butonuna tikla ve açıldığını doğrula
        anasayfa.billingAdresADD.click();
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.firstNameYazi.isDisplayed());

        anasayfa.adressesFirstNameAs.click();
        Actions actions = new Actions(Driver.getDriver());
        actions.sendKeys(Keys.PAGE_DOWN,Keys.PAGE_DOWN).perform();
        anasayfa.countryRegionAs.click();
        ReusableMethods.bekle(3);
        anasayfa.countryregionTextAs.sendKeys("Albania",Keys.TAB);
        ReusableMethods.bekle(1);

        anasayfa.streetAdressAs.sendKeys("a");
        ReusableMethods.bekle(10);
        anasayfa.townCityAs.sendKeys("b");
        ReusableMethods.bekle(1);
        anasayfa.stateCountAs.click();
        ReusableMethods.bekle(1);
        anasayfa.stateCounttextAs.sendKeys("Berat",Keys.TAB);
        ReusableMethods.bekle(5);
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        anasayfa.postcode_zipAs.sendKeys("123");
        ReusableMethods.bekle(5);

    }*/
}
