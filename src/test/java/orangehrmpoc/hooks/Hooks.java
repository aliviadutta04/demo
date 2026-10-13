package orangehrmpoc.hooks;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.automation.remarks.video.RecorderFactory;
import com.automation.remarks.video.recorder.IVideoRecorder;
import com.automation.remarks.video.recorder.VideoRecorder;
import orangehrmpoc.api.EmployeeApi;
import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;
import orangehrmpoc.utils.TestContext;
import orangehrmpoc.utils.VideoConverter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hooks {
    private static final Logger LOGGER = Logger.getLogger(Hooks.class.getName());
    private final TestContext context;
    private IVideoRecorder videoRecorder;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        if (!scenario.getSourceTagNames().contains("@loginNegative")) {
            ConfigReader.get("username");
            ConfigReader.get("password");
        }
        DriverFactory.initializeDriver();
        if (VideoRecorder.conf().videoEnabled()) {
            videoRecorder = RecorderFactory.getRecorder(VideoRecorder.conf().recorderType());
            videoRecorder.start();
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.isInitialized()) {
                try {
                    byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                            .getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");

                    String fileName = scenario.getName()
                            .replaceAll("[^a-zA-Z0-9-_]", "_") + ".png";
                    Path directory = Path.of("target", "screenshots");
                    Files.createDirectories(directory);
                    Files.write(directory.resolve(fileName), screenshot);
                } catch (IOException | RuntimeException e) {
                    LOGGER.log(Level.WARNING, "Unable to capture failure screenshot", e);
                }
            }

            attachVideo(scenario);

            if (context.employeeNumber != null
                    || (context.employeeCreationAttempted && context.employeeId != null)) {
                boolean cleaned = false;
                if (context.employeeNumber != null) {
                    try {
                        Response response = new EmployeeApi(context.adminCookies)
                                .deleteEmployee(context.employeeNumber);
                        cleaned = response.statusCode() == 200 || response.statusCode() == 204;
                        if (!cleaned) {
                            LOGGER.warning("API cleanup returned HTTP " + response.statusCode()
                                    + "; falling back to UI cleanup");
                        }
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.WARNING, "API cleanup failed; falling back to UI cleanup", e);
                    }
                }

                if (!cleaned && context.employeeId != null && DriverFactory.isInitialized()) {
                    new orangehrmpoc.pages.EmployeePage().deleteEmployee(context.employeeId);
                    cleaned = true;
                }
                if (!cleaned) {
                    throw new IllegalStateException("Employee cleanup could not be completed");
                }

                context.employeeNumber = null;
                context.employeeId = null;
                context.employeeCreationAttempted = false;
            }
        } catch (RuntimeException e) {
            if (!scenario.isFailed()) {
                throw e;
            }
            LOGGER.log(Level.WARNING, "Scenario cleanup failed after a test failure", e);
        } finally {
            DriverFactory.quitDriver();
        }
    }

    private void attachVideo(Scenario scenario) {
        if (videoRecorder == null) {
            return;
        }

        try {
            String scenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
            String recordingName = scenarioName + "_" + UUID.randomUUID();
            java.io.File recording = videoRecorder.stopAndSave(recordingName);
            if (recording != null && recording.isFile()) {
                Path attachment = recording.toPath();
                String contentType = "application/octet-stream";
                try {
                    attachment = VideoConverter.toBrowserCompatibleMp4(attachment);
                    contentType = "video/mp4";
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING,
                            "Unable to convert video; attaching the original AVI for download", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING,
                            "Video conversion was interrupted; attaching the original AVI for download", e);
                }
                scenario.attach(Files.readAllBytes(attachment), contentType, attachment.getFileName().toString());
            } else {
                LOGGER.warning("Video recorder returned no file for scenario " + scenario.getName());
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.WARNING, "Unable to attach scenario video", e);
        } finally {
            videoRecorder = null;
        }
    }
}
