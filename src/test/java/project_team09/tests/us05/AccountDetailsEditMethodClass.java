package project_team09.tests.us05;

import org.openqa.selenium.Keys;
import project_team09.pages.meryem.AccountDetailsPageMeryem;
import project_team09.utilities.ConfigReader;
import project_team09.utilities.Driver;
import project_team09.utilities.ExtentReport;
import project_team09.utilities.ReusableMethods;

public class AccountDetailsEditMethodClass extends ExtentReport {
        public static void signIn() {
                extentTest = extentReports.createTest("US05", "AccountDetailsAddressEdit");
                // stiye git
                Driver.getDriver().get(ConfigReader.getProperty("allovercommerceUrl"));
                extentTest.info("SITEYE GIDILDI");
                // sign ın butonuna tıkla
                AccountDetailsPageMeryem accountDetails = new AccountDetailsPageMeryem();
                accountDetails.signInButton.click();
                extentTest.info("SIGNIN BUTONUNA TIKLANDI");
                // acılan ekranda username kutusuna username'i gir
                // password kutusuna password'u gir
                // sign ın butonuna tıkla
                String username = ConfigReader.getProperty("usernamemeMo");
                String password = ConfigReader.getProperty("passwordMo");
                accountDetails.usernameButton.sendKeys(username, Keys.TAB, password, Keys.ENTER);
                ReusableMethods.bekle(2);
                extentTest.info("ACILAN EKRANDA USERNAME KUTUSUNA USERNAME, PASSWORD KUTUSUNA PASSWORD YAZILDI VE SIGNIN BUTONUNA TIKLANDI");
                // Driver.getDriver().navigate().refresh();

                /*
                 * testcase excell de aşadaki step eksik
                 */
                // acılan sayfadaki sag üstteki signout sekmesine tıkla.(My accont sayfası
                // acılmalı)
                ReusableMethods.scrollEnd();
                ReusableMethods.click(accountDetails.myAccountButton);
                ReusableMethods.bekle(3);
                extentTest.info("ACILAN SAYFADA FOOTER BOLUMUNE INILDI VE MYACCONT BUTONUNA TIKLANDI");
                // Account Details web elementine tıkla
                accountDetails.accountDetailsButton.click();
                ReusableMethods.bekle(3);
                extentTest.info("ACCOUNT DETAILS WEB ELEMENTINE TIKLANDI");

        }
}
