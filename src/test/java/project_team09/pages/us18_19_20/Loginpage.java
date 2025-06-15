package project_team09.pages.us18_19_20;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class Loginpage {



        public Loginpage() {
            PageFactory.initElements(Driver.getDriver(), this);
        }
        //anasayfada ki sing in butonu
        @FindBy(xpath = "//a[@class='login inline-type']")
        public WebElement singIn;
        //sing in username veya email kutusu
        @FindBy(xpath = "//input[@id='username']")
        public WebElement userName;
        //uyelik girisi yapmak icin basilan sing in (login)
        @FindBy(xpath = "//button[@name='login']")
        public WebElement loginButton;
        //giris yaptigini dogrulamak icin sing out locatoru
        @FindBy(xpath = "//div[@class='account']")
        public WebElement singOut;


        @FindBy(xpath="(//*[@name='password'])[1]")
        public WebElement password;




        //home page signOut**
        @FindBy(xpath = "//span[text()='Sign Out']")
        public WebElement signOut;

        @FindBy(xpath = "(//h2)[1]")
        public WebElement myAccountYazisi;
        @FindBy(xpath = "//*[text()='Dashboard']")
        public WebElement dashboardText;
        @FindBy(xpath = "//*[.='Store Manager']")
        public WebElement storeManagerText;
        @FindBy(xpath = "//h2[1]")//Store Manager başlığı
        public WebElement storeManagerBaslik;

    }


