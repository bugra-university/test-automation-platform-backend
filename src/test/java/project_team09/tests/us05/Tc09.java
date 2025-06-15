package project_team09.tests.us05;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.meryem.AccountDetailsPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Tc09 extends ExtentReport {
        @Test
        public void test01() {
                extentTest = extentReports.createTest("US05", "AccountDetailsAddressEdit");

                AccountDetailsEditMethodClass.signIn();
                /*
                 * ilk 6 step bu methodun içinde
                 */
                AccountDetailsPageMeryem page = new AccountDetailsPageMeryem();
                // "First name " kutusuna firstname yazma
                page.firstName.clear();
                extentTest.info("FIRST NAME KUTUSUNA FIRST NAME YAZILMADI");
                // "last name" kutusuna lastname yaz
                page.lastName.clear();
                String lastName = ConfigReader.getProperty("degistirilecekLastName");
                page.lastName.sendKeys(lastName);
                extentTest.info("LAST NAME KUTUSUNA LAST NAME YAZILDI");
                // "Display name" kutusuna görünmesini istediğin ismini yaz
                page.displayName.clear();
                String displayName = ConfigReader.getProperty("degistirilecekDisplayName");
                page.displayName.sendKeys(displayName);
                extentTest.info("DISPLAY KUTUSUNA DISPLAY NAME YAZILDI");
                // "Email address" kutusuna mailini yaz
                page.emailAddress.clear();
                String email = ConfigReader.getProperty("email1");
                page.emailAddress.sendKeys(email);
                extentTest.info("EMAİL KUTUSUNA EMAİL YAZILDI");
                // "Biography" bölümüne birseyler yaz /yazma zorunlu değil

                // "Password change" kısmındaki ilk kutuyu mevcut sifreni yaz
                String currentPassword = ConfigReader.getProperty("passwordMo");
                page.currentPasswword.sendKeys(currentPassword);
                extentTest.info("PASSWORD CHANGE KISMINDAKİ ILK KUTUYA MEVCUT SIFRE YAZILDI");
                // "Password change" kısmındaki ikinci kutuya yeni sifreyi yaz
                String newPassword = ConfigReader.getProperty("degistirilecekPassword");
                page.newPasswword.sendKeys(newPassword);
                extentTest.info("PASSWORD CHANGE KISMINDAKİ IKINCI KUTUYA MEVCUT SIFRE YAZILDI");
                // "Password change" kısmındaki ücüncü kutuya yeni sifreyi tekrar yaz
                String confirmPassword = ConfigReader.getProperty("degistirilecekPassword");
                page.confirmPasswword.sendKeys(confirmPassword);
                extentTest.info("PASSWORD CHANGE KISMINDAKİ UCUNCU KUTUYA MEVCUT SIFRE YAZILDI");
                // "SAVE CHANGES" butanona tıkla
                ReusableMethods.bekle(2);
                JavascriptExecutor js = (JavascriptExecutor) Driver.getDriver();
                WebElement saveButton = page.saveChangesButton;
                js.executeScript("arguments[0].click();", saveButton);
                extentTest.info("SAVE CHANGES BUTTONUNA TIKLANDI");
                // "FIRST NAME is a required field." mesajının cıktığını doğrula
                String expectedMsj = "FIRST NAME is a required field.";
                WebElement errorElement = Driver.getDriver()
                                .findElement(By.xpath("//*[contains(@class, 'woocommerce-error')]"));
                Assert.assertTrue(errorElement.isDisplayed(), "Error message is not displayed");
                String actualText = errorElement.getText();
                Assert.assertTrue(actualText.contains(expectedMsj),
                                "Expected error message: '" + expectedMsj + "' but got: '" + actualText + "'");
                extentTest.info("FIRST NAME is a required field. MESAJININ CIKTIGI DOGRULANDI");

        }
}
