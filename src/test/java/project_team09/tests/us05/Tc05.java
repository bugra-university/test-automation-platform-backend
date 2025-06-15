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

public class Tc05 extends ExtentReport {
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
                // "Display name" kutusuna görünmesini istediğin ismini yazma
                page.displayName.clear();
                extentTest.info("DISPLAY KUTUSUNA DISPLAY NAME YAZILMADI");
                // "Email address" kutusuna mailini yaz
                page.emailAddress.clear();
                String email = ConfigReader.getProperty("email1");
                page.emailAddress.sendKeys(email);
                extentTest.info("EMAİL KUTUSUNA EMAİL YAZILDI");
                // "SAVE CHANGES" butanona tıkla
                ReusableMethods.bekle(2);
                JavascriptExecutor js = (JavascriptExecutor) Driver.getDriver();
                WebElement saveButton = page.saveChangesButton;
                js.executeScript("arguments[0].click();", saveButton);
                extentTest.info("SAVE CHANGES BUTTONUNA TIKLANDI");
                // "DİSPLAY NAME is a required field." mesajının cıktığını doğrula
                String expectedMsj = "Display name is a required field.";
                WebElement errorElement = Driver.getDriver().findElement(By.xpath(
                                "//*[contains(@class, 'woocommerce-error') and contains(text(), 'Display name')]"));

                // Hem görünürlük hem de mesaj içeriği kontrolü
                Assert.assertTrue(errorElement.isDisplayed(), "Hata mesajı görünmüyor");
                Assert.assertTrue(errorElement.getText().contains(expectedMsj),
                                "Beklenen hata mesajı görüntülenmedi. Beklenen: " + expectedMsj + ", Gerçek: "
                                                + errorElement.getText());

                extentTest.info("DISPLAY NAME is a required field. MESAJININ CIKTIGI DOGRULANDI");

        }
}
