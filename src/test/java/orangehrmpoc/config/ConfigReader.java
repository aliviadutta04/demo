package orangehrmpoc.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static final Properties dotEnv = new Properties();

    static {
        load("/config/config.properties");
        loadDotEnv();
        load("/config/config-" + configuredEnvironment() + ".properties");
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

    private static String configuredEnvironment() {
        String environment = System.getProperty("env");
        if (environment == null || environment.isBlank()) {
            environment = System.getenv(toEnvironmentVariable("env"));
        }
        if (environment == null || environment.isBlank()) {
            environment = dotEnv.getProperty(toEnvironmentVariable("env"));
        }
        if (environment == null || environment.isBlank()) {
            environment = properties.getProperty("environment");
        }
        if (environment == null || environment.isBlank()) {
            throw new MissingConfigurationException("Property is not configured: environment");
        }
        return environment.trim().toLowerCase(Locale.ROOT);
    }

    private static void loadDotEnv() {
        Path workingDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        String configuredPaths = System.getProperty("env.file.paths");
        if (configuredPaths == null || configuredPaths.isBlank()) {
            configuredPaths = System.getenv("ORANGEHRM_ENV_FILE_PATHS");
        }
        if (configuredPaths == null || configuredPaths.isBlank()) {
            configuredPaths = properties.getProperty("env.file.paths");
        }
        Path localEnvFile = Arrays.stream(configuredPaths.split(","))
                .map(String::trim)
                .map(workingDirectory::resolve)
                .filter(Files::exists)
                .findFirst()
                .orElse(null);

        if (localEnvFile == null) {
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
        return resolveValue(key, System.getProperties(), System.getenv(), dotEnv, properties);
    }

    static String resolveValue(
            String key,
            Properties systemProperties,
            Map<String, String> environmentVariables,
            Properties dotenvProperties,
            Properties profileProperties) {
        String systemValue = systemProperties.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentValue = environmentVariables.get(toEnvironmentVariable(key));
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        String dotEnvValue = dotenvProperties.getProperty(toEnvironmentVariable(key));
        if (dotEnvValue != null && !dotEnvValue.isBlank()) {
            return dotEnvValue;
        }

        String value = profileProperties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new MissingConfigurationException("Property is not configured: " + key);
        }
        return value;
    }

    static String toEnvironmentVariable(String key) {
        return "ORANGEHRM_" + key.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replace('-', '_').replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
