package orangehrmpoc.steps;
import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;
import orangehrmpoc.pages.LoginPage;

import io.cucumber.java.en.Given;
public class LoginSteps {
     private LoginPage loginPage;


    @Given("I open the employee application")
    public void openApplication() {

        DriverFactory
                .getDriver()
                .get(
                    ConfigReader.get("baseUrl"));
    }


    @Given("I login with valid credentials")
    public void loginWithValidCredentials() {

        loginPage =
                new LoginPage();

        loginPage.login();
    }
}
