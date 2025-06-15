package project_team09.tests.us14;

import com.github.javafaker.Faker;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.us12_13_14.AlloverCommercePage;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;


public class TC01_VendorOlarakUrunEkleme extends ExtentReport{
    AlloverCommercePage page = new AlloverCommercePage();
    Actions actions = new Actions(Driver.getDriver());


    @Test
    public void test01() {
        extentTest = ExtentReport.extentReports.createTest
                ("US_14 TC_01 Vendor Olarak Urun Ekleme", "Vendor urun ekleyebilmeli");

        //Kullanıcı adrese gider
        Driver.getDriver().get(ConfigReader.getProperty("alloverCommerceUrlME"));
        ReusableMethods.bekle(1);
        extentTest.info("https://allovercommerce.com/ adresine gidilir");

        //sign in a tıklar
        page.signInGirisME.click();
        ReusableMethods.bekle(1);
        extentTest.info("sign in a tıklar");

        //Gecerli e mail adresini girer
        page.eMailKutusuME.sendKeys(ConfigReader.getProperty("emailSignInME"));
        ReusableMethods.bekle(1);
        extentTest.info("Gecerli e mail adresini girer");

        //sifre girer
        page.passWordKutusuME.sendKeys(ConfigReader.getProperty("emailKutusuSifreME"));
        ReusableMethods.bekle(1);
        extentTest.info("sifre girer");

        //sign in butonuna a tıklar
        page.signInButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("sign in butonuna a tıklar");

        //SignOut butonuna tıklar
        page.signOutButtonME.click();
        ReusableMethods.bekle(1);
        extentTest.info("SignOut butonuna tıklar");

        //"STORAGE MANAGER" e tiklar
        page.storeManagerME.click();
        ReusableMethods.bekle(1);
        extentTest.info("STORAGE MANAGER'e tiklar");

        //"Products" a tiklar
        actions.scrollByAmount(300, 300);
        actions.moveToElement(page.productsME).perform();
        ReusableMethods.bekle(1);
        extentTest.info("Products'a tiklar");

        //"Add New" e tiklar
        actions.moveToElement(page.addNewME).click().perform();
        ReusableMethods.bekle(1);
        actions.scrollByAmount(200, 200);
        ReusableMethods.bekle(1);
        extentTest.info("Add New'e tiklar");

        //"Simple Product" drop-down menu'ye tiklar
        ReusableMethods.ddmIndex(page.simpleProductME, 1);
        ReusableMethods.bekle(1);
        ReusableMethods.ddmIndex(page.simpleProductME, 2);
        ReusableMethods.bekle(1);
        ReusableMethods.ddmIndex(page.simpleProductME, 3);
        ReusableMethods.bekle(2);
        extentTest.info("Simple Product drop-down menu'ye tiklar");

        //Simple Product seceneginin goruntulendigini dogrular
        Select select = new Select(page.simpleProductME);
        select.selectByIndex(0);
        System.out.println(select.getFirstSelectedOption().getText());
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "Simple Product");
        ReusableMethods.bekle(1);
        extentTest.info("Simple Product seceneginin goruntulendigini dogrular");

        //Variable Product seceneginin goruntulendigini dogrular
        select.selectByIndex(1);
        System.out.println(select.getFirstSelectedOption().getText());
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "Variable Product");
        ReusableMethods.bekle(1);
        extentTest.info("Variable Product seceneginin goruntulendigini dogrular");

        //Grouped Product seceneginin goruntulendigini dogrular
        select.selectByIndex(2);
        System.out.println(select.getFirstSelectedOption().getText());
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "Grouped Product");
        ReusableMethods.bekle(1);
        extentTest.info("Grouped Product seceneginin goruntulendigini dogrular");

        //External/Affiliate Product seceneginin goruntulendigini dogrular
        select.selectByIndex(3);
        System.out.println(select.getFirstSelectedOption().getText());
        Assert.assertEquals(select.getFirstSelectedOption().getText(), "External/Affiliate Product");
        ReusableMethods.bekle(1);
        extentTest.info("External/Affiliate Product seceneginin goruntulendigini dogrular");

        //Ekranin sagindaki add photo alanina tiklar
        page.fotografEkleBuyukKisimME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Ekranin sagindaki add photo alanina tiklar");

        //"SELECT FILES" button'una tiklar
        page.selectFilesButtonME.click();
        ReusableMethods.uploadFile("\"C:\\Users\\baklava ailesi\\Desktop\\Screenshot 2023-09-18 233651.png\"");
        ReusableMethods.bekle(1);
        extentTest.info("SELECT FILES button'una tiklar");

        //"SELECT" button'una tiklar
        page.selectButonuME.click();
        ReusableMethods.bekle(1);
        extentTest.info("SELECT button'una tiklar");

        //Ekledigimiz foto'nun foto alaninda gorunur oldugunu dogrular
        Assert.assertTrue(page.yuklenenResimME.isDisplayed());
        ReusableMethods.bekle(1);
        extentTest.info("Ekledigimiz foto'nun foto alaninda gorunur oldugunu dogrular");

        //Sayfanin sagindaki foto ekleme olumundeki kucuk foto alanina tiklar
        page.kucukResimEklemeButonuME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Sayfanin sagindaki foto ekleme olumundeki kucuk foto alanina tiklar");

        //Cikan foto galeri ekranindan bir foto secer
        page.kucukResimIcinFotografSecmeME.click();
        ReusableMethods.bekle(1);
        extentTest.info("Cikan foto galeri ekranindan bir foto secer");

        //"ADD TO GALLERY" button'una tiklar
        page.addToGalleryButonuME.click();
        ReusableMethods.bekle(1);
        extentTest.info("ADD TO GALLERY button'una tiklar");

        //"Product Title" alanina "title" yazar
        page.productTitleKutusuME.sendKeys(ConfigReader.getProperty("productTitle"));
        ReusableMethods.bekle(1);
        extentTest.info("Product Title alanina title yazar");

        //"Short Description" alanina kisa bir tanim girilir
        Driver.getDriver().switchTo().frame(page.iframeShortME);
        ReusableMethods.click(page.shortDescriptionKutusuME);
        page.shortDescriptionKutusuME.sendKeys(ConfigReader.getProperty("shortDescription"));
        Driver.getDriver().switchTo().parentFrame();
        ReusableMethods.bekle(1);
        actions.scrollByAmount(300, 300).perform();
        extentTest.info("Short Description alanina kisa bir tanim girilir");

        //"Description" alanina bir tanim girilir
        //Driver.getDriver().switchTo().frame(page.iframeDescME);
        //ReusableMethods.click(page.DescriptionKutusuME);
        //page.DescriptionKutusuME.sendKeys(ConfigReader.getProperty("descriptionGomlek"));
        //Driver.getDriver().switchTo().parentFrame();
        //ReusableMethods.bekle(1);
        //extentTest.info("Description alanina bir tanim girilir");

        //"Categories" alanindan uc kategori secilir
        page.categoriesKutusuS1ME.click();
        page.categoriesKutusuS2ME.click();
        page.categoriesKutusuS3ME.click();
        extentTest.info("Categories alanindan uc kategori secilir");

        //"Categories" alanindan secilen alan deselect edilir
        page.categoriesKutusuS1ME.click();
        extentTest.info("Categories alanindan secilen alan deselect edilir");

        //"Add new category" e tiklar
        //actions.scrollByAmount(300,300).perform();
        page.addNewCategoryButonuME.click();
        extentTest.info("Add new category'e tiklar");

        //Category name girilir
        Faker faker = new Faker();
        String faker1 = faker.name().fullName();
        page.categoryNameButonuME.sendKeys(faker1);
        extentTest.info("Category name girilir");

        //"ADD" button'una tiklanir
        page.addButonuME.click();
        ReusableMethods.visibleWait(page.addNewCategoryButonuME, 10);
        ReusableMethods.bekle(6);
        extentTest.info("ADD button'una tiklanir");

        //"Categories" alanina girilen isim dogrulanir
        Assert.assertEquals(page.categoriesIlkUrunME.getText(), " " + faker1);
        ReusableMethods.bekle(3);
        actions.scrollByAmount(300, 300);
        extentTest.info("Categories alanina girilen isim dogrulanir");

        // "Product brands" alanindan bir urun secilir
        page.productBrandsS1ME.click();
        extentTest.info(" Product brands alanindan bir urun secilir");

        //"Product brands" alanindan bir urun secilmez
        page.productBrandsS1ME.click();
        extentTest.info("Product brands alanindan bir urun secilmez");

        //"Add new Product brands" a tiklar
        page.addNewProductsBrandsButonuME.click();
        extentTest.info("Add new Product brands'a tiklar");

        //Products brands name yazar
        String faker2 = faker.name().lastName();
        page.productsBrandNameME.sendKeys(faker2);
        extentTest.info("Products brands name yazar");

        //"ADD" button'una tiklar
        page.productBrandADDButonuME.click();
        ReusableMethods.visibleWait(page.addNewProductsBrandsButonuME, 10);
        ReusableMethods.bekle(6);
        extentTest.info("ADD button'una tiklar");

        // "Product brands" alanina urun ekledigini dogrular
        Assert.assertEquals(page.productBrandIlkUrunME.getText(), " " + faker2);
        ReusableMethods.scroll(page.submitButtonME);
        extentTest.info("Product brands alanina urun ekledigini dogrular");

        ReusableMethods.bekle(2);
        // "SUBMIT" button'una tiklar
        page.submitButtonME.click();
        extentTest.info("SUBMIT button'una tiklar");

        //"Product Succesfully Published" yazisinin ciktigini dogrular
        Assert.assertTrue(page.productSuccesfullyPublishedYazisiME.isDisplayed());
        ReusableMethods.bekle(2);
        extentTest.info("Product Succesfully Published yazisinin ciktigini dogrular");
    }
}
