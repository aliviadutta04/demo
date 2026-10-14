package orangehrmpoc.driver;

import orangehrmpoc.config.ConfigReader;

import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();

    public static void initializeDriver() {

        String browser = ConfigReader.get("browser");

        boolean headless =
                Boolean.parseBoolean(
                        ConfigReader.get("headless"));

        WebDriver webDriver = switch (browser.trim().toLowerCase(Locale.ROOT)) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser + ". Supported browsers: chrome, firefox, edge");
        };
        driver.set(webDriver);
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments(ConfigReader.get("browser.chrome.headlessArgument"));
        }
        options.addArguments(ConfigReader.get("browser.chrome.arguments").split(","));
        options.addArguments("--window-size=" + browserSize());
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments(ConfigReader.get("browser.firefox.headlessArgument"));
        }
        options.addArguments("--width=" + ConfigReader.get("browser.width"),
                "--height=" + ConfigReader.get("browser.height"));
        options.addPreference(ConfigReader.get("browser.firefox.notificationPreference"),
            Boolean.parseBoolean(ConfigReader.get("browser.firefox.notificationsEnabled")));
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments(ConfigReader.get("browser.edge.headlessArgument"));
        }
        options.addArguments(ConfigReader.get("browser.edge.arguments").split(","));
        options.addArguments("--window-size=" + browserSize());
        return options;
    }

    private static String browserSize() {
        return ConfigReader.get("browser.width") + "," + ConfigReader.get("browser.height");
    }

    public static WebDriver getDriver() {

        if (driver.get() == null) {
            throw new RuntimeException(
                    "WebDriver is not initialized");
        }

        return driver.get();
    }

    public static boolean isInitialized() {
        return driver.get() != null;
    }

    public static void quitDriver() {

        if (driver.get() != null) {

            driver.get().quit();
            driver.remove();
        }
    }
}