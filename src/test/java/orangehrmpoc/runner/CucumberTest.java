package orangehrmpoc.runner;

import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.cucumber.testng.CucumberOptions;
import orangehrmpoc.config.ConfigReader;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import orangehrmpoc.utils.VideoFileManager;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import orangehrmpoc.retry.RetryAnalyzer;

@CucumberOptions(
                features = "src/test/resources/features",
                glue = {
                                "orangehrmpoc.steps",
                                "orangehrmpoc.hooks"
                })
public class CucumberTest {
        private static final ReentrantReadWriteLock SCENARIO_EXECUTION_LOCK =
                        new ReentrantReadWriteLock(true);

        private TestNGCucumberRunner testNGCucumberRunner;

        @BeforeClass(alwaysRun = true)
        public void setUpClass() {
                testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }

        @Test(groups = "cucumber", dataProvider = "scenarios", retryAnalyzer = RetryAnalyzer.class)

        public void runScenario(
                        PickleWrapper pickleWrapper,
                        FeatureWrapper featureWrapper) {
                boolean parallelScenario = isParallelScenario(pickleWrapper.getPickle().getTags());
                Lock scenarioLock = parallelScenario
                                ? SCENARIO_EXECUTION_LOCK.readLock()
                                : SCENARIO_EXECUTION_LOCK.writeLock();
                scenarioLock.lock();
                try {
                        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
                } finally {
                        scenarioLock.unlock();
                }
        }

        static boolean isParallelScenario(Collection<String> tags) {
                return tags.contains(ConfigReader.get("parallel.tag"));
        }

        @DataProvider(parallel = true)
        public Object[][] scenarios() {

                return testNGCucumberRunner.provideScenarios();
        }

        @AfterClass(alwaysRun = true)
        public void tearDownClass() {
                if (testNGCucumberRunner != null) {
                        testNGCucumberRunner.finish();
                }

                VideoFileManager.keepLatestVideo();
        }
}