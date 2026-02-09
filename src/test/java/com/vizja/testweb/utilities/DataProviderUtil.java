package com.vizja.testweb.utilities;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {
    @DataProvider
    public Object[][] isimler() {
        return new Object[][] { { "erol" }, { "evren" }, { "ahmet" }, { "mehmet" } };
    }
}
