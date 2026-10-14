package orangehrmpoc.steps;
import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;
import orangehrmpoc.pages.LoginPage;
import orangehrmpoc.utils.TestContext;
import orangehrmpoc.utils.TestDataGenerator;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Cookie;
import java.util.stream.Collectors;
import static org.testng.Assert.assertTrue;

public class LoginSteps {
    private final TestContext context;
    private LoginPage loginPage;

    public LoginSteps(TestContext context) {
        this.context = context;
    }


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
        context.adminCookies = DriverFactory.getDriver().manage().getCookies().stream()
            .collect(Collectors.toUnmodifiableMap(Cookie::getName, Cookie::getValue));
    }

    @Given("I am logged in as the configured ESS user")
    public void loginAsEssUser() {
        loginPage = new LoginPage();
        loginPage.logout();
        loginPage.login(
                ConfigReader.get("essUsername"),
                ConfigReader.get("essPassword"));
    }

    @When("I submit invalid login credentials")
    public void submitInvalidLoginCredentials() {
        loginPage = new LoginPage();
        loginPage.submitInvalidCredentials(
            TestDataGenerator.invalidUsername(),
            TestDataGenerator.invalidPassword());
    }

    @Then("the invalid login error should be displayed")
    public void invalidLoginErrorShouldBeDisplayed() {
        assertTrue(loginPage.isLoginErrorDisplayed(), "Invalid credentials error was not displayed");
    }

    @Then("the login form should remain displayed")
    public void loginFormShouldRemainDisplayed() {
        assertTrue(loginPage.isLoginFormDisplayed(),
            "The login form should remain visible after rejected credentials");
    }
}
