package orangehrmpoc.pages;
import orangehrmpoc.config.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebElement;
public class LoginPage extends BasePage
    {
         private By username =
            By.name("username");

    private By password =
            By.name("password");

    private By loginButton =
            By.cssSelector(
                    "button[type='submit']");
    private By loginError = By.cssSelector(".oxd-alert-content-text");


    public void login() {
                login(ConfigReader.get("username"), ConfigReader.get("password"));
        }

        public void login(String usernameValue, String passwordValue) {
                submitCredentials(usernameValue, passwordValue);
                wait.until(ExpectedConditions.urlContains("/dashboard"));
        }

        public void submitInvalidCredentials(String usernameValue, String passwordValue) {
                submitCredentials(usernameValue, passwordValue);
        }

        private void submitCredentials(String usernameValue, String passwordValue) {
        enterText(
                username,
                                usernameValue);

        enterText(
                password,
                                passwordValue);

        click(loginButton);
        }

        public boolean isLoginErrorDisplayed() {
                return isDisplayed(loginError);
        }

        public boolean isLoginFormDisplayed() {
                return isDisplayed(username) && isDisplayed(password);
    }

        public void logout() {
                click(By.className("oxd-userdropdown-tab"));
                click(By.linkText("Logout"));
        }
}
