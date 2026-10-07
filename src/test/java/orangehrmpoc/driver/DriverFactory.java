package orangehrmpoc.driver;
import  orangehrmpoc.config.ConfigReader;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();


    public static void initializeDriver() {

        String browser =
                ConfigReader.get("browser");

        boolean headless =
                Boolean.parseBoolean(
                        ConfigReader.get("headless"));


        if (browser.equalsIgnoreCase("chrome")) {

            ChromeOptions options =
                    new ChromeOptions();

            if (headless) {

                options.addArguments(
                        "--headless=new");
            }

            options.addArguments(
                    "--start-maximized");

            options.addArguments(
                    "--disable-notifications");


            driver.set(
                    new ChromeDriver(options));
        }

        else {

            throw new RuntimeException(
                    "Browser not supported: "
                            + browser);
        }
    }


    public static WebDriver getDriver() {

        if (driver.get() == null) {

            throw new RuntimeException(
                    "WebDriver is not initialized");
        }

        return driver.get();
    }


    public static void quitDriver() {

        if (driver.get() != null) {

            driver.get().quit();

            driver.remove();
        }
    }
}
