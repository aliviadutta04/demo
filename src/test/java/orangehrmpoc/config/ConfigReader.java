package orangehrmpoc.config;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static final Properties dotEnv = new Properties();

    static {
        String environment = System.getProperty("env", "dev").toLowerCase();
        load("/config/config.properties");
        load("/config/config-" + environment + ".properties");
        loadDotEnv();
    }

    private static void load(String resource) {
        try (var input = ConfigReader.class.getResourceAsStream(resource)) {
            if (input == null) {
                if (resource.endsWith("config.properties")) {
                    throw new IllegalStateException("Missing configuration resource: " + resource);
                }
                throw new IllegalArgumentException("Unknown environment configuration: " + resource);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration: " + resource, e);
        }
    }

    private static void loadDotEnv() {
        Path workingDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        Path projectEnvFile = workingDirectory.resolve("demo/.env");
        Path localEnvFile = Files.exists(projectEnvFile)
                ? projectEnvFile
                : workingDirectory.resolve(".env");

        if (!Files.exists(localEnvFile)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(localEnvFile, StandardCharsets.UTF_8)) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                String assignment = trimmedLine.startsWith("export ")
                        ? trimmedLine.substring("export ".length()).trim()
                        : trimmedLine;
                int separator = assignment.indexOf('=');
                if (separator <= 0) {
                    throw new IllegalArgumentException("Invalid .env entry; expected KEY=value");
                }

                String key = assignment.substring(0, separator).trim();
                String value = assignment.substring(separator + 1).trim();
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                                || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                dotEnv.setProperty(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load local .env file: " + localEnvFile, e);
        }
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentValue = System.getenv(toEnvironmentVariable(key));
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        String dotEnvValue = dotEnv.getProperty(toEnvironmentVariable(key));
        if (dotEnvValue != null && !dotEnvValue.isBlank()) {
            return dotEnvValue;
        }

        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new MissingConfigurationException("Property is not configured: " + key);
        }
        return value;
    }

    private static String toEnvironmentVariable(String key) {
        return "ORANGEHRM_" + key.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replace('-', '_').toUpperCase();
    }
}
