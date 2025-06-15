package project_team09.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

public class Driver {

    private Driver() {

        /*
         * SINGLETON PATTERN:(Tekli Kullanim)
         * Driver class'indan obje olusturmanin onune gecmek icin buradaki public olan
         * gizli constructor'i private
         * yaptik.Boylece Driver class'indan obje olusturmanin onune gectik..
         */
    }

    static WebDriver driver;

    public static WebDriver getDriver() {

        /*
         * Driver'i her çağırdığımızda yeni bir pencere açmamasi için bir if bloğu ile
         * bu işi çözdük.
         * if(driver == null) ile eğer driver'a değer atanmamış ise driver'a değerleri
         * ata, tekrar driver
         * çağrıldığında driver da değer olduğu için direk driver'i return et.
         * Dolayısıyla driver'ı ikinci kez
         * çağırdığımızda açık gördüğü browser da yani aynı sayfada belirtilen web
         * sitene gider.
         */

        /*
         * Testlerimizi farklı driver'larda çalıştırmak için her seferinde getDriver()
         * methodu içindeki
         * driver ayarlarını değiştirmek yerine aşağıdaki gibi switch case ile
         * .properties dosyasında browser
         * olarak belirttiğimiz key'in değeri ne ise browser o driver ile çalışacaktır
         */

        if (driver == null) {
            // Docker Selenium Grid URL'ini kontrol et
            String seleniumHub = System.getenv("SELENIUM_HUB_URL");

            if (seleniumHub != null && !seleniumHub.isEmpty()) {
                try {
                    // Selenium Grid üzerinden çalıştırma için özel ayarlar
                    org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");

                    System.out.println("Selenium Grid'e bağlanılıyor: " + seleniumHub);
                    driver = new org.openqa.selenium.remote.RemoteWebDriver(
                            new java.net.URL(seleniumHub),
                            options);
                } catch (java.net.MalformedURLException e) {
                    e.printStackTrace();
                    // Hata durumunda yerel tarayıcıya geç
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                }
            } else {
                // Docker dışında normal çalışma için mevcut ayarlar
                switch (ConfigReader.getProperty("browser")) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                        break;
                    case "edge":
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver();
                        break;
                    case "safari":
                        WebDriverManager.safaridriver().setup();
                        driver = new SafariDriver();
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver();
                        break;
                    default:
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                }
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }
        return driver;
    }

    public static void closeDriver() {

        /*
         * Driver'i direk kapatip tekrar bir sayfaya gitmek istersek hata alırız. Çünkü
         * Driver'in yeniden oluşma şartı değer atanmamış olması. Close yaptıktan sonra
         * driver hala
         * değer atılı durumda gözükür dolayısıyla yeniden driver'ı oluşturabilmesi için
         * yani getDriver()
         * methodundaki oluşma şartına uyabilmesi için driver'i tekrar null' a
         * eşitlememiz yani
         * kapandıktan sonra boş olduğunu belirtmemiz gerekir
         */

        if (driver != null) {
            // driver.close();
            driver = null;
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}