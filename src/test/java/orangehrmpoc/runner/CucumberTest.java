package orangehrmpoc.runner;

import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.cucumber.testng.CucumberOptions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import orangehrmpoc.utils.VideoFileManager;


import orangehrmpoc.retry.RetryAnalyzer;

@CucumberOptions(features = "src/test/resources/features",

                glue = {
                                "orangehrmpoc.steps",
                                "orangehrmpoc.hooks"
                },

                tags = "@role",

                plugin = {
                                "pretty",
                                "html:target/cucumber-report/CucumberTest.html",
                                "timeline:target/cucumber-report/cucumberTest-timeline"
                },

                monochrome = true, publish = false)
public class CucumberTest {

        private static final String ENVIRONMENT = "dev";
        private TestNGCucumberRunner testNGCucumberRunner;

        @BeforeClass(alwaysRun = true)
        public void setUpClass() {
                System.setProperty("env", System.getProperty("env", ENVIRONMENT));
                testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        }

        @Test(groups = "cucumber", dataProvider = "scenarios", retryAnalyzer = RetryAnalyzer.class)

        public void runScenario(
                        PickleWrapper pickleWrapper,
                        FeatureWrapper featureWrapper) {

                testNGCucumberRunner.runScenario(
                                pickleWrapper.getPickle());
        }

        @DataProvider(parallel = false)
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