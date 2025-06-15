package project_team09.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    /*
    .properties dosyasindaki datalara ulasabilmek icin return type'i String olan parametreli
    static bir method olustururuz. Bu method icinde Properties class'indan bir obje olustururz.
    Ve FileInputStream ile .properties uzantili dosyamizi akisa alir, Properties class'indan olusturdugumuz
    objeye akisa aldigimiz dosyayi load() methodu ile yukleriz. Ve son olarak properties class'indan olusturdugumuz
    obje ile getProperty() methodunu kullanir ve girilen key'in degerini bize return eder.
    */

    public static String getProperty(String key) {
        Properties properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream("configuration.properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(key);
    }
}
