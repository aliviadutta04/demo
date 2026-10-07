package orangehrmpoc.hooks;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import orangehrmpoc.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class Hooks {
    @Before
    public void setUp() {
        DriverFactory.initializeDriver();
    }

    @After
    public void tearDown(Scenario scenario) {

        if (scenario.isFailed()) {

            byte[] screenshot =
                    ((TakesScreenshot) DriverFactory.getDriver())
                            .getScreenshotAs(OutputType.BYTES);

            scenario.attach(
                    screenshot,
                    "image/png",
                    "Failure Screenshot"
            );

            try {

                String fileName =
                        scenario.getName()
                                .replaceAll("[^a-zA-Z0-9-_]", "_")
                                + ".png";

                Path directory =
                        Path.of("target", "screenshots");

                Files.createDirectories(directory);

                Files.write(
                        directory.resolve(fileName),
                        screenshot
                );

            } catch (IOException e) {

                System.err.println(
                        "Unable to save failure screenshot: "
                                + e.getMessage()
                );
            }
        }

        DriverFactory.quitDriver();
    }
}
