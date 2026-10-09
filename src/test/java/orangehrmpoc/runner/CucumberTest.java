package orangehrmpoc.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.cucumber.testng.CucumberOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.automation.remarks.video.annotations.Video;
import com.automation.remarks.testng.VideoListener;
import org.testng.annotations.Listeners;
import orangehrmpoc.utils.VideoFileManager;


import orangehrmpoc.retry.RetryAnalyzer;

@Listeners(VideoListener.class)
@CucumberOptions(features = "src/test/resources/features",

                glue = {
                                "orangehrmpoc.steps",
                                "orangehrmpoc.hooks"
                },

                tags = "@negative",

                plugin = {
                                "pretty",
                                "html:target/cucumber-report/CucumberTest.html",
                                "timeline:target/cucumber-report/cucumberTest-timeline"
                },

                monochrome = true, publish = false)
public class CucumberTest {

        private TestNGCucumberRunner testNGCucumberRunner;

        @BeforeClass(alwaysRun = true)
        public void setUpClass() {

                testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }

        @Video
        @Test(groups = "cucumber", dataProvider = "scenarios", retryAnalyzer = RetryAnalyzer.class)

        public void runScenario(
                        PickleWrapper pickleWrapper,
                        FeatureWrapper featureWrapper) {

                testNGCucumberRunner.runScenario(
                                pickleWrapper.getPickle());
        }

        @DataProvider
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