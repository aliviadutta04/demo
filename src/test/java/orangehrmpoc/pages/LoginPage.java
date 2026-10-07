package orangehrmpoc.pages;
import orangehrmpoc.config.ConfigReader;

import org.openqa.selenium.By;
public class LoginPage extends BasePage
    {
         private By username =
            By.name("username");

    private By password =
            By.name("password");

    private By loginButton =
            By.cssSelector(
                    "button[type='submit']");


    public void login() {

        enterText(
                username,
                ConfigReader.get("username"));

        enterText(
                password,
                ConfigReader.get("password"));

        click(loginButton);
    }
}
