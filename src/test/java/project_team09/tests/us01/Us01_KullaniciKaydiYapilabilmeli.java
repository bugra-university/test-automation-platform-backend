package project_team09.tests.us01;


import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import project_team09.pages.Us01Us02Us03.Anasayfa;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Us01_KullaniciKaydiYapilabilmeli extends ExtentReport {


    @Test
    public void tc01_KullaniciKayit() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","İstenen tüm bilgiler girildiğinde kayıt işlemi gerçekleşmeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");


        //email kutusuna kayıtlı olmayan bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Şifre kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys(ConfigReader.getProperty("signInPassword1"));
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        extentTest.info("Sign Up butonuna tıklandı");

        ReusableMethods.bekle(2);


        //Kayıt işleminin gerçekleştiğini doğrula

        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Kayıt işleminin gerçekleştiğini doğrulandı");

        //sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }


    @Test
    public void tc02_withoutUsernameNotRegister() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusunu boş bırak
        extentTest.info("username kutusu boş bırakıldı");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();


        //Sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");

    }

    @Test
    public void tc03_withoutEmailNotRegister() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","email alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");


        //email kutusunu boş bırak
        extentTest.info("email alanı boş bırakıldı");


        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        ReusableMethods.bekle(2);
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(5);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }

    @Test
    public void tc04_withoutPasswordNotRegister() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","password alanı boş bırakıldığında kayıt işlemi gerçekleşmemeli test edilidi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusunu boş bırak
        extentTest.info("şifre kutusu boş bırakıldı");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }


    @Test
    public void tc05_WithoutIagreeClickNotRegister() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı", "I agree to the privacy policy\" kontrol kutusuna tıklanmadığında SignUp işlemi gerçekleşmemeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");


        //"I agree to the privacy policy" kontrol kutusuna tıklama
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklanmadı");



        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();


        //Sayfayı kapat

        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }


    @Test
    public void tc06_hataliemailileKayitOlma() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı", "Email alanına @ sembolü eklenmeden girilen mail ile kayıt işlemi gerçekleşmemeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //@ işareti olmadan email gir
        anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliEmail1"));
        extentTest.info("@ işareti olmadan email girildi");


        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");


        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();


        //Sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");



    }


    @Test
    public void tc07_withoutComEmailIleKayitOlma() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("kullanıcı kaydı","Email alanına .com eklenmeden kayıt işlemi gerçekleşmemeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //.com  olmadan email gir
        anasayfa.emailAs.sendKeys(ConfigReader.getProperty("hataliemail2"));
        extentTest.info(".com  olmadan email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");


        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        softAssert.assertAll();

        //Sayfayı kapat

        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");



    }


    @Test
    public void tc08_sekizChrctrPasswordkayit() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 8 karakterli bir şifre girilerek kayıt olunabilmeli");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 8 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password8");
        extentTest.info(" 8 karakterli şifre girildi");


        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula

        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");

        //Sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }

    @Test
    public void tc09_dokuzChrctrPasswordkayit() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 9 karakterli bir şifre girilerek kayıt olunabilmeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");


        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 9 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password9");
        extentTest.info("9 karakterli bir şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula

        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");

        //Sayfayı kapat

        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");
    }


    @Test
    public void tc10_yediChrctrPasswordkayitOlma() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına 7 karakterli bir şifre girilerek kayıt olunamamalı test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna 7 karakterli bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("password7");
        extentTest.info("7 karakterli bir şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");


        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertTrue(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Siteye kayıt yapılamadığı doğrulandı");
        ReusableMethods.tumSayfaResmi("us01tc10kayit");
        extentTest.info("hatalı bilgiler ile kayıt yapıldı");
        softAssert.assertAll();

        //Sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");
    }


    @Test
    public void tc11_passwordSadeceRakamlaKayitOlma() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","Password alanına sadece rakamlardan oluşan bir şifre girilerek kayıt olunamamalı test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna bir kullanıcı adı gir
        anasayfa.usernameAs.sendKeys(faker.name().firstName());
        extentTest.info("yeni bir username girildi");

        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna sadece rakamlardan şifre gir
        anasayfa.passwordSignUpAs.sendKeys("passwordRakam");
        extentTest.info("sadece rakamlardan oluşan şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Siteye kayıt yapılamadığını doğrula
        ReusableMethods.bekle(2);
        softAssert.assertFalse(anasayfa.popUpMesajAs.isDisplayed());
        extentTest.info("Kayıt işleminin gerçekleşmediği doğrulandı");
        ReusableMethods.tumSayfaResmi("us01tc11kayit");
        extentTest.info("hatalı bilgilerle kayıt yapıldı");
        softAssert.assertAll();

        //Sayfayı kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }

    @Test
    public void tc12_buyukkucukHarfRakamUsernameKayit() {
        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanına büyük harf, küçük harf ve rakam girilerek kayıt olunabilmeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna büyük harf, küçük harf ve rakam içeren kullanıcı adı gir

        anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername2"));
        extentTest.info("username kutusuna büyük harf, küçük harf ve rakam içeren kullanıcı adı girildi");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        ReusableMethods.bekle(10);

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        extentTest.info("Sign Up butonuna tıklandı");


        //Kayıt işleminin gerçekleştiğini doğrula

        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Siteye kayıt yapıldığı doğrulandı");

        //Sayfayi kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }


    @Test
    public void tc13_usernameOzelkarakterKayit() {

        Anasayfa anasayfa = new Anasayfa();
        Faker faker = new Faker();
        SoftAssert softAssert = new SoftAssert();
        extentTest = ExtentReport.extentReports.createTest("Kullanıcı kaydı","username alanına özel karakter girilerek kayıt olunabilmeli test edildi");

        //Web sitesine git ve doğrula
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));
        softAssert.assertTrue(anasayfa.registerAs.isDisplayed());
        extentTest.info("web sitesine gidildi ve sayfanın açıldığı doğrulandı");

        //Registera tıkla
        anasayfa.registerAs.click();
        ReusableMethods.bekle(3);
        extentTest.info("Registera tıklandı");

        //username kutusuna  özel karakter(*) içeren kullanıcı adı gir

        anasayfa.usernameAs.sendKeys(ConfigReader.getProperty("yeniUsername3"));
        extentTest.info("username kutusuna  özel karakter(*) içeren kullanıcı adı girildi");


        //email kutusuna bir mail gir
        anasayfa.emailAs.sendKeys(faker.internet().emailAddress());
        extentTest.info("yeni bir email girildi");

        //Password kutusuna bir şifre gir
        anasayfa.passwordSignUpAs.sendKeys("signInPassword1");
        extentTest.info("şifre girildi");

        //"I agree to the privacy policy" kontrol kutusuna tıkla
        anasayfa.iAgreeButonAs.click();
        extentTest.info("I agree to the privacy policy kontrol kutusuna tıklandı");

        //Sign Up butonuna tıkla
        anasayfa.signUpYeniKayit.click();
        ReusableMethods.bekle(2);
        extentTest.info("Sign Up butonuna tıklandı");

        //Kayıt işleminin gerçekleştiğini doğrula
        ReusableMethods.tumSayfaResmi("us01tc13kayit");
        softAssert.assertTrue(anasayfa.signOutAs.isDisplayed());
        extentTest.info("Kayıt işleminin gerçekleştiğini doğrulanmak istendi fakat hata bulundu");
        softAssert.assertAll();

        //Sayfayi kapat
        Driver.closeDriver();
        extentTest.info("sayfa kapatıldı");


    }
}
