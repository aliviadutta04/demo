package orangehrmpoc.runner;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(

        features = "src/test/resources/features",

        glue = {
                "orangehrmpoc.steps",
                "orangehrmpoc.hooks"
        },

        tags = "@negative",

        plugin = {
                "pretty",
                "html:target/cucumber-report/cucumber.html"
        },

        monochrome = true,

        publish = false
)


public class CucumberTest {
    
}
