package orangehrmpoc.pages;

import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasePage {
        private static final Logger LOGGER = Logger.getLogger(BasePage.class.getName());
        protected static final By PIM_MENU = By.cssSelector("a[href*='/pim/viewPimModule']");
        protected WebDriver driver;

        protected WebDriverWait wait;

        public BasePage() {

                driver = DriverFactory.getDriver();

                int timeout = Integer.parseInt(
                                ConfigReader.get("timeout"));

                wait = new WebDriverWait(
                                driver,
                                Duration.ofSeconds(timeout));
        }

        protected void click(By locator) {
                click(locator, false);
        }

        protected void clickWithJavascriptFallback(By locator) {
                click(locator, true);
        }

        private void click(By locator, boolean useJavascriptFallback) {
                waitForFormLoaderToDisappear();

                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                                element);

                wait.until(ExpectedConditions.elementToBeClickable(locator));
                waitForFormLoaderToDisappear();

                try {
                        element.click();
                } catch (ElementClickInterceptedException e) {
                        if (!useJavascriptFallback) {
                                throw e;
                        }
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
        }

        protected void navigateTo(String url) {
                driver.get(url);
                wait.until(ExpectedConditions.urlToBe(url));
        }

        protected void waitForFormLoaderToDisappear() {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".oxd-form-loader")));
        }

        protected void enterText(
                        By locator,
                        String value) {

                WebElement element = wait.until(
                                ExpectedConditions
                                                .visibilityOfElementLocated(
                                                                locator));

                element.click();
                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                element.sendKeys(Keys.BACK_SPACE);
                element.sendKeys(value);
        }

        protected String getAttribute(By locator, String attribute) {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                                .getAttribute(attribute);
        }

        protected boolean hasAttributeValue(By locator, String attribute, String expectedValue) {
                return wait.until(ExpectedConditions.attributeToBe(locator, attribute, expectedValue));
        }

        protected void waitUntilInvisible(By locator) {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }

        protected String waitForUrlContains(String fragment) {
                wait.until(ExpectedConditions.urlContains(fragment));
                return driver.getCurrentUrl();
        }

        protected boolean isDisplayed(By locator) {
                return isDisplayed(locator, Duration.ofSeconds(Integer.parseInt(
                                ConfigReader.get("timeout"))));
        }

        protected boolean isDisplayed(By locator, Duration timeout) {
                try {
                        return new WebDriverWait(driver, timeout).until(
                                        ExpectedConditions
                                                        .visibilityOfElementLocated(
                                                                        locator))
                                        .isDisplayed();
                } catch (TimeoutException e) {
                        LOGGER.log(Level.SEVERE, "Timed out waiting for element to be visible: " + locator, e);
                        throw e;
                }
        }

        protected boolean isAbsent(By locator, Duration timeout) {
                return new WebDriverWait(driver, timeout).until(
                                ExpectedConditions.invisibilityOfElementLocated(locator));
        }
}
