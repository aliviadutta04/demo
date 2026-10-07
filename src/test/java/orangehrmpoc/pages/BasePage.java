package orangehrmpoc.pages;

import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
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

                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

                ((JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                                element);

                wait.until(ExpectedConditions.elementToBeClickable(locator));

                element.click();
        }

        protected void enterText(
                        By locator,
                        String value) {

                WebElement element = wait.until(
                                ExpectedConditions
                                                .visibilityOfElementLocated(
                                                                locator));

                element.clear();

                element.sendKeys(value);
        }

        protected String getText(By locator) {

                return wait.until(
                                ExpectedConditions
                                                .visibilityOfElementLocated(
                                                                locator))
                                .getText();
        }

        protected boolean isDisplayed(By locator) {

                try {

                        return wait.until(
                                        ExpectedConditions
                                                        .visibilityOfElementLocated(
                                                                        locator))
                                        .isDisplayed();

                } catch (Exception e) {

                        return false;
                }
        }
}
