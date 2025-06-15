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

public class Tc03 extends ExtentReport {
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
                // "SAVE CHANGES" butanona tıkla
                ReusableMethods.bekle(2);
                JavascriptExecutor js = (JavascriptExecutor) Driver.getDriver();
                WebElement saveButton = page.saveChangesButton;
                js.executeScript("arguments[0].click();", saveButton);
                extentTest.info("SAVE CHANGES BUTTONUNA TIKLANDI");
                // "FIRST NAME is a required field." mesajının cıktığını doğrula
                String expectedMsj = "FIRST NAME is a required field.";
                WebElement actualMsjElement = Driver.getDriver().findElement(
                                By.xpath("//*[@class='woocommerce-error' and contains(text(), 'First name')]"));
                String actualMsj = actualMsjElement.getText();

                // Hem mesajın görünür olduğunu hem de içeriğini kontrol et
                Assert.assertTrue(actualMsjElement.isDisplayed(), "Hata mesajı görünmüyor");
                Assert.assertTrue(actualMsj.contains(expectedMsj), "Beklenen hata mesajı görüntülenmedi. Beklenen: "
                                + expectedMsj + ", Gerçek: " + actualMsj);

                extentTest.info("FIRST NAME is a required field. MESAJININ CIKTIGI DOGRULANDI");

        }
}
