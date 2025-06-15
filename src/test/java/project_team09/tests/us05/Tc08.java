package project_team09.tests.us05;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.meryem.AccountDetailsPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class Tc08 extends ExtentReport {
        @Test
        public void test01() {
                extentTest = extentReports.createTest("US05", "AccountDetailsAddressEdit");

                AccountDetailsEditMethodClass.signIn();
                /*
                 * ilk 6 step bu methodun içinde
                 */
                AccountDetailsPageMeryem page = new AccountDetailsPageMeryem();
                // "First name " kutusuna firstname yaz
                page.firstName.clear();
                String firstName = ConfigReader.getProperty("degistirilecekFirstName");
                page.firstName.sendKeys(firstName);
                extentTest.info("FIRST NAME KUTUSUNA FIRST NAME YAZILDI");
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
                extentTest.info("PASSWORD CHANGE KISMINDAKİ IKINCIKUTUYA MEVCUT SIFRE YAZILDI");
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
                // "Account details changed successfully." mesajının cıktığını doğrula
                String expectedMsj = "Account details changed successfully.";
                String actualMsj = page.successfulyMesaji.getText();
                Assert.assertEquals(expectedMsj, actualMsj);
                extentTest.info("Account details changed successfully. MESAJININ CIKTIGI DOGRULANDI");

        }
}
