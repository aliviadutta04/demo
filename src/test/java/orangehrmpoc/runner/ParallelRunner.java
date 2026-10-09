package orangehrmpoc.runner;

import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.cucumber.testng.CucumberOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import orangehrmpoc.retry.RetryAnalyzer;
import com.automation.remarks.video.annotations.Video;
import com.automation.remarks.testng.VideoListener;
import org.testng.annotations.Listeners;
import orangehrmpoc.utils.VideoFileManager;

@Listeners(VideoListener.class)
@CucumberOptions(features = "src/test/resources/features",

                glue = {
                                "orangehrmpoc.steps",
                                "orangehrmpoc.hooks"
                },

                tags = "@parallel",

                plugin = {
                                "pretty",
                                "html:target/cucumber-report/parallel.html",
                                "timeline:target/cucumber-report/parallel-timeline"
                },

                monochrome = true, publish = false)
public class ParallelRunner {

        private TestNGCucumberRunner testNGCucumberRunner;

        @BeforeClass(alwaysRun = true)
        public void setUpClass() {

                testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }

        @Test(groups = "cucumber", dataProvider = "scenarios", retryAnalyzer = RetryAnalyzer.class)
        @Video
        public void runScenario(
                        PickleWrapper pickleWrapper,
                        FeatureWrapper featureWrapper) {

                testNGCucumberRunner.runScenario(
                                pickleWrapper.getPickle());
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