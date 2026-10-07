package orangehrmpoc.config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ConfigReader {
        private static final Properties properties = new Properties();

    static {

        try {

            FileInputStream file =
                    new FileInputStream(
                            "src/test/resources/config/config.properties");

            properties.load(file);

            file.close();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load config.properties", e);
        }
    }


    public static String get(String key) {

    String systemValue = System.getProperty(key);

    if (systemValue != null) {
        return systemValue;
    }

    String value = properties.getProperty(key);

    if (value == null) {
        throw new RuntimeException(
                "Property not found in config.properties: " + key
        );
    }

    return value;
}
    
}
