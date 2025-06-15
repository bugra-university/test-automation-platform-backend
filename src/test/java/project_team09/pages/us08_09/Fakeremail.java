package project_team09.pages.us08_09;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class Fakeremail {
    public Fakeremail() {
        PageFactory.initElements(Driver.getDriver(), this);
    }


    //FAKE LOCATE
    @FindBy(xpath = "//*[@class='color cetc']") public WebElement fakeMailKutu;

    @FindBy(id = "predmet") public static WebElement mailVerivacitonCode;
    @FindBy(id = "confirm_pwd") public WebElement confirmPwd;
    @FindBy(xpath = "//*[@class='wcfm-message wcfm-error']") public WebElement verivacitonInvalidMesaj;
    @FindBy(xpath ="//*[text()='Please provide a valid email address.']") public WebElement eksikMailHataMesaji;
    @FindBy(xpath = "//div[@class='wcfm-message wcfm-error']") public WebElement pswEksikMesaji;
    @FindBy(xpath = "//*[@class='wcfm-message wcfm-error']") public WebElement kisaPwdMesaj;
    @FindBy(xpath = "//*[text()='Registration']") public WebElement dogrulamaRegistration;
    @FindBy(xpath = "//*[@class= 'hidden-xs hidden-sm klikaciRadek newMail'][1]") public WebElement succesMesaj;

    @FindBy(id = "schranka") public static WebElement fakeMailTiklama;


}
