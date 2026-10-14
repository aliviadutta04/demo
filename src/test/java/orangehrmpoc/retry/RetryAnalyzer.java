package orangehrmpoc.retry;

import io.cucumber.testng.PickleWrapper;
import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.config.MissingConfigurationException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.logging.Logger;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(RetryAnalyzer.class.getName());
    private static final Path RETRY_REPORT = Path.of(ConfigReader.get("retry.report"));
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = Integer.parseInt(ConfigReader.get("retry.maxAttempts"));

    @Override
    public boolean retry(ITestResult result) {
        if (!isRetryAllowed(retryCount, MAX_RETRY_COUNT, result.getThrowable())) {
            if (hasMissingConfigurationCause(result.getThrowable())) {
            LOGGER.severe("Not retrying scenario with missing configuration: " + result.getName());
            } else {
                LOGGER.severe("Retry limit reached for scenario " + scenarioName(result));
            }
            return false;
        }

        int attempt = retryCount + 1;
        retryCount++;
        recordFailure(result, attempt, true);
        LOGGER.warning("Retrying failed scenario " + scenarioName(result) + " (attempt "
                + retryCount + " of " + MAX_RETRY_COUNT + ")");
        return true;
    }

    private static synchronized void recordFailure(ITestResult result, int attempt, boolean willRetry) {
        try {
            Files.createDirectories(RETRY_REPORT.getParent());
            if (Files.notExists(RETRY_REPORT)) {
                Files.writeString(RETRY_REPORT,
                        "timestamp,scenario,attempt,maxRetries,outcome" + System.lineSeparator(),
                        StandardOpenOption.CREATE_NEW);
            }
            String row = csv(Instant.now().toString()) + ","
                    + csv(scenarioName(result)) + ","
                    + attempt + ","
                    + MAX_RETRY_COUNT + ","
                    + (willRetry ? "retrying" : "exhausted")
                    + System.lineSeparator();
            Files.writeString(RETRY_REPORT, row, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.warning("Unable to record flaky-test event: " + e.getMessage());
        }
    }

    private static String scenarioName(ITestResult result) {
        for (Object parameter : result.getParameters()) {
            if (parameter instanceof PickleWrapper pickleWrapper) {
                return pickleWrapper.getPickle().getName();
            }
        }
        return result.getName();
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    static boolean isRetryAllowed(int retryCount, int maximumRetries, Throwable failure) {
        return retryCount < maximumRetries && !hasMissingConfigurationCause(failure);
    }

    private static boolean hasMissingConfigurationCause(Throwable failure) {
        while (failure != null) {
            if (failure instanceof MissingConfigurationException) {
                return true;
            }
            failure = failure.getCause();
        }
        return false;
    }
}