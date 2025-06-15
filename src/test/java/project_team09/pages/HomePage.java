package project_team09.pages;

import org.openqa.selenium.support.PageFactory;
import project_team09.utilities.Driver;

public class HomePage {
    public HomePage() {
        PageFactory.initElements(Driver.getDriver(), this);
    }

}
