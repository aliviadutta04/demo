package orangehrmpoc.runner;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import orangehrmpoc.retry.RetryAnalyzer;
import orangehrmpoc.utils.VideoFileManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"orangehrmpoc.steps", "orangehrmpoc.hooks"},
        tags = "@smoke or @negative")
public class ParallelCucumberRunner {
    private TestNGCucumberRunner cucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        cucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @Test(dataProvider = "scenarios", retryAnalyzer = RetryAnalyzer.class)
    public void runScenario(PickleWrapper pickle, FeatureWrapper feature) {
        cucumberRunner.runScenario(pickle.getPickle());
    }

    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return cucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (cucumberRunner != null) {
            cucumberRunner.finish();
        }
        VideoFileManager.keepLatestVideo();
    }
}