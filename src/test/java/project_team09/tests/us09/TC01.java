package project_team09.tests.us09;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;
import project_team09.pages.us08_09.Fakeremail;
import project_team09.pages.us08_09.VendorGiris;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

public class TC01 {
    @Test
    public void tc01() {

        VendorGiris vendorGiris = new VendorGiris();
        Fakeremail fakeremail = new Fakeremail();

        // Web sitesine git
        Driver.getDriver().get(ConfigReader.getProperty("allowerCommerceUrl"));

        // Registera butonuna tıkla

        vendorGiris.singIN.click();
        ReusableMethods.bekle(3);
        // sign up button tıkla
        vendorGiris.signUp.click();
        ReusableMethods.bekle(3);

        // Become vendor Butonuna tıkla
        ReusableMethods.bekle(3);
        vendorGiris.becomeVendor.click();
        ReusableMethods.bekle(3);

        // Email alanina email gir.
        Driver.getDriver().switchTo().newWindow(WindowType.WINDOW);
        ReusableMethods.window(1);
        Driver.getDriver().get(ConfigReader.getProperty("FakerUrl"));
        ReusableMethods.bekle(3);
        fakeremail.fakeMailKutu.click();
        ReusableMethods.window(0);
        ReusableMethods.bekle(3);
        vendorGiris.email.click();
        ReusableMethods.bekle(3);
        Actions actions = new Actions(Driver.getDriver());
        actions.keyDown(Keys.CONTROL).sendKeys("v").release().perform();
        ReusableMethods.bekle(2);
        vendorGiris.reSendCode.click();
        ReusableMethods.bekle(3);

        // Mail adresine gelen kodu Verification Code text kutusuna gir
        // ReusableMethods.window(1);//sekmeye geçer
        ReusableMethods.bekle(10);
        Fakeremail.fakeMailTiklama.click();

        String mailKod = Fakeremail.mailVerivacitonCode.getText();

        ReusableMethods.bekle(2);
        String[] sadeceKod = mailKod.split(" ");

        System.out.print("KOD: " + sadeceKod[6]);
        ReusableMethods.bekle(2);
        ReusableMethods.window(0);
        vendorGiris.verificationCodeClick.sendKeys(sadeceKod[6]);

    }
}
