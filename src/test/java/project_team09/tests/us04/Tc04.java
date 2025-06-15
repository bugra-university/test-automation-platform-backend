package project_team09.tests.us04;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.meryem.ShippingAddressPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Tc04 extends ExtentReport {


    @Test
    public void test01() {
        extentTest =extentReports.createTest("US04","ShipphingAddressEdit");


        ShipphingAddressEditMethodClass editMethod=new ShipphingAddressEditMethodClass();
        editMethod.signIn();
/*
ilk 7 step methodun içinde
 */




        ShippingAddressPageMeryem shipping = new ShippingAddressPageMeryem();
//"First name" kutusuna first name ekle
        String firstName=ConfigReader.getProperty("degistirilecekFirstName");
        shipping .firstNameBox.clear();
        shipping .firstNameBox.sendKeys(firstName);
        extentTest.info("FIRST NAME KUTUSUNA FIRST NAME YAZILDI");
//"Last name" kutusuna last name ekle
        String lastName=ConfigReader.getProperty("degistirilecekLastName");
        shipping.lastNameBox.clear();
        shipping.lastNameBox.sendKeys(lastName);
        ReusableMethods.bekle(2);
        extentTest.info("LAST NAME KUTUSUNA LAST NAME YAZILDI");
//"Company name (optional)" kutusunu boş bırak
        shipping.companyNameBox.clear();
        extentTest.info("COMPANY NAME KUTUSUNU BOS BIRAK");
//"Country/Region" ddm den secim yap
        Actions actions = new Actions(Driver.getDriver());
        actions.sendKeys(Keys.PAGE_DOWN).perform();
        ReusableMethods.bekle(2);
        WebElement ddmCountry= shipping.countryDdm;
        Select select=new Select(ddmCountry);
        select.selectByVisibleText(ConfigReader.getProperty("degistirilecekCountry"));
        extentTest.info("COUNTRY DDM DEN SECIM YAPILDI");
//"Street address" ilk butona house number ve street name yaz
        String addressIlkBox=ConfigReader.getProperty("degistirilecekAdressIlkBox");
        shipping.streetAddressFirstBox.clear();
        shipping.streetAddressFirstBox.sendKeys(addressIlkBox);
        ReusableMethods.bekle(2);
        extentTest.info("STREET ADRESS BOLUMUNDEKI ILK KUTU DOLDURULDU");
//"Street address" ikinci butona apartman ismi yaz
        String addressIkinciBox=ConfigReader.getProperty("degistirilecekAdresIkinciBox");
        shipping.streetAddressSeccondBox.clear();
        shipping.streetAddressSeccondBox.sendKeys(addressIkinciBox);
        extentTest.info("STREET ADRESS BOLUMUNDEKI IKINCI KUTU DOLDURULDU");
//"Postcode / ZIP" kısmına posta kodu yaz
        String postcode=ConfigReader.getProperty("degistirilecekPostcode");
        ReusableMethods.bekle(2);
        shipping.postcodeBox.clear();
        ReusableMethods.bekle(2);
        shipping.postcodeBox.sendKeys(postcode);
        extentTest.info("POSTCODE KUTUSUNA POSTCODE YAZILDI");
//"Town / City" kısmına sehrini yaz
        String town=ConfigReader.getProperty("degistirilecekTown");
        shipping.townBox.clear();
        ReusableMethods.bekle(2);
        shipping.townBox.sendKeys(town);
        extentTest.info("TOWN/CITY BOLUMUNE CITY YAZILDI");
//"Province" ddm den sehirini seç
        Select select1=new Select(shipping.provinceDdm);
        //shipping.provinceDdm.clear();
        ReusableMethods.bekle(2);
        select1.selectByVisibleText(ConfigReader.getProperty("degistirilecekProvince"));
        extentTest.info("PROVINCE DDM DEN SECIM YAPILDI");
//"SAVE ADRESS" butonuna tıkla
        shipping.saveAddressButton.click();
        extentTest.info("SAVE ADRESS BUTONUNA TIKLANDI");
//"Address changed successfully." mesajının çıktığını doğrula
        String expectedMsj="Address changed successfully.";
        String actualMsj=shipping.dogrulamaMsj.getText();
        Assert.assertEquals(expectedMsj,actualMsj);

        extentTest.info("ADDRESS CHANGED SUCCESSFULLY. MESAJININ CIKTIGI DOGRULANDI");

    }

}
