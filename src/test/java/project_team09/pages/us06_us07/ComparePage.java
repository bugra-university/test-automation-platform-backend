package project_team09.pages.us06_us07;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class ComparePage {

    public ComparePage () {
        PageFactory.initElements(Driver.getDriver(),this);
    }

    @FindBy(xpath = "(//*[@title='Compare'])[1]")
    public WebElement compareButton;

    @FindBy(xpath = "//*[@*='btn btn-dark btn-rounded']")
    public WebElement startCompare;

    @FindBy(xpath = "//*[@*='page-title']")
    public WebElement compareYazisi;

    @FindBy(xpath = "(//*[@*='w-icon-times-solid'])[4]")
    public WebElement xButton;

    @FindBy(xpath = "(//*[@*='w-icon-times-solid'])[1]")
    public WebElement xButtonx;

    @FindBy(xpath = "//*[text()='No products added to the compare']")
    public WebElement compareDogrulama;

}
