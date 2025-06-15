package project_team09.utilities;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {

    @DataProvider
    public Object[][] isimler() {
        return new Object[][]{{"erol"}, {"evren"}, {"ahmet"}, {"mehmet"}};
    }
}