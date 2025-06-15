package project_team09.tests.us05;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import project_team09.pages.meryem.AccountDetailsPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ReusableMethods;

import java.time.Duration;

public class Tc07 {
        @Test
        public void test01() {
                AccountDetailsEditMethodClass.signIn();
                AccountDetailsPageMeryem page = new AccountDetailsPageMeryem();
                WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(10));

                // Kişisel bilgileri doldur
                fillPersonalInfo(page);

                // Biography bölümü için iframe işlemleri
                WebElement iframe = wait
                                .until(ExpectedConditions.presenceOfElementLocated(By.id("user_description_ifr")));
                ReusableMethods.scroll(iframe);
                ReusableMethods.visibleWait(iframe, 10);

                // iframe'e geçiş yap
                Driver.getDriver().switchTo().frame(iframe);

                // Biography'yi doldur
                page.ifarmeBiography.clear();
                page.ifarmeBiography.sendKeys(ConfigReader.getProperty("biography"));

                // Ana sayfaya geri dön
                Driver.getDriver().switchTo().defaultContent();

                // Save Changes butonuna tıkla
                WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(page.saveChangesButton));
                ReusableMethods.click(saveButton);

                // Başarı mesajını kontrol et
                String expectedMsj = "Account details changed successfully.";
                WebElement successMessage = wait.until(ExpectedConditions.visibilityOf(page.successfulyMesaji));
                Assert.assertEquals(successMessage.getText(), expectedMsj,
                                "Başarı mesajı beklendiği gibi görüntülenmedi");
        }

        private void fillPersonalInfo(AccountDetailsPageMeryem page) {
                // First Name
                page.firstName.clear();
                page.firstName.sendKeys(ConfigReader.getProperty("degistirilecekFirstName"));

                // Last Name
                page.lastName.clear();
                page.lastName.sendKeys(ConfigReader.getProperty("degistirilecekLastName"));

                // Display Name
                page.displayName.clear();
                page.displayName.sendKeys(ConfigReader.getProperty("degistirilecekDisplayName"));

                // Email
                page.emailAddress.clear();
                page.emailAddress.sendKeys(ConfigReader.getProperty("email1"));
        }
}
