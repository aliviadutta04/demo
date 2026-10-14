package orangehrmpoc.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

import java.util.Map;
import java.util.Properties;
import org.testng.annotations.Test;

public class ConfigReaderTest {
    @Test
    public void resolvesSystemPropertyBeforeEnvironmentAndFiles() {
        Properties systemProperties = properties("browser.width", "1600");
        Properties dotenvProperties = properties("ORANGEHRM_BROWSER_WIDTH", "1400");
        Properties profileProperties = properties("browser.width", "1280");

        String result = ConfigReader.resolveValue(
                "browser.width",
                systemProperties,
                Map.of("ORANGEHRM_BROWSER_WIDTH", "1366"),
                dotenvProperties,
                profileProperties);

        assertEquals(result, "1600");
    }

    @Test
    public void resolvesEnvironmentBeforeDotenvAndProfile() {
        String result = ConfigReader.resolveValue(
                "browser.width",
                new Properties(),
                Map.of("ORANGEHRM_BROWSER_WIDTH", "1366"),
                properties("ORANGEHRM_BROWSER_WIDTH", "1400"),
                properties("browser.width", "1280"));

        assertEquals(result, "1366");
    }

    @Test
    public void resolvesDotenvBeforeProfile() {
        String result = ConfigReader.resolveValue(
                "browser.width",
                new Properties(),
                Map.of(),
                properties("ORANGEHRM_BROWSER_WIDTH", "1400"),
                properties("browser.width", "1280"));

        assertEquals(result, "1400");
    }

    @Test
    public void reportsMissingConfiguration() {
        expectThrows(MissingConfigurationException.class, () -> ConfigReader.resolveValue(
                "unset.value", new Properties(), Map.of(), new Properties(), new Properties()));
    }

    @Test
    public void mapsDottedKeysToEnvironmentVariables() {
        assertEquals(ConfigReader.toEnvironmentVariable("browser.width"), "ORANGEHRM_BROWSER_WIDTH");
    }

    private static Properties properties(String key, String value) {
        Properties properties = new Properties();
        properties.setProperty(key, value);
        return properties;
    }
}