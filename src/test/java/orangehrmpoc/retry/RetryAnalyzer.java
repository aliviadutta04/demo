package orangehrmpoc.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    private static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < MAX_RETRY_COUNT) {

            retryCount++;

            System.out.println("======================================");
            System.out.println("RETRYING FAILED CUCUMBER SCENARIO");
            System.out.println("Scenario: " + result.getName());
            System.out.println(
                    "Retry attempt: "
                            + retryCount
                            + " of "
                            + MAX_RETRY_COUNT
            );
            System.out.println("======================================");

            return true;
        }

        System.out.println("======================================");
        System.out.println("MAX RETRIES REACHED");
        System.out.println("Scenario: " + result.getName());
        System.out.println("======================================");

        return false;
    }
}