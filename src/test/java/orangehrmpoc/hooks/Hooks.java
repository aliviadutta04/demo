package orangehrmpoc.hooks;
import orangehrmpoc.driver.DriverFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
public class Hooks {
      @Before
    public void setUp() {

        DriverFactory.initializeDriver();
    }


    @After
    public void tearDown(
            Scenario scenario) {

        if (scenario.isFailed()) {

            byte[] screenshot =
                    ((TakesScreenshot)
                            DriverFactory.getDriver())
                            .getScreenshotAs(
                                    OutputType.BYTES);

            scenario.attach(
                    screenshot,
                    "image/png",
                    "Failure Screenshot");
        }


        DriverFactory.quitDriver();
    }
}
