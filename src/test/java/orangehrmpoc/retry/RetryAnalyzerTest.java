package orangehrmpoc.retry;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import orangehrmpoc.config.MissingConfigurationException;
import org.testng.annotations.Test;

public class RetryAnalyzerTest {
    @Test
    public void allowsRetryWhileAttemptsRemain() {
        assertTrue(RetryAnalyzer.isRetryAllowed(0, 2, new AssertionError("transient failure")));
    }

    @Test
    public void stopsRetryingWhenAttemptLimitIsReached() {
        assertFalse(RetryAnalyzer.isRetryAllowed(2, 2, new AssertionError("failure")));
    }

    @Test
    public void doesNotRetryMissingConfigurationEvenWhenAttemptsRemain() {
        Throwable failure = new RuntimeException("setup failed",
                new MissingConfigurationException("missing setting"));

        assertFalse(RetryAnalyzer.isRetryAllowed(0, 2, failure));
    }

    @Test
    public void doesNotRetryWhenRetriesAreDisabled() {
        assertFalse(RetryAnalyzer.isRetryAllowed(0, 0, new AssertionError("failure")));
    }
}