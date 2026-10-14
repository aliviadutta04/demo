package orangehrmpoc.utils;

import orangehrmpoc.config.ConfigReader;
import java.util.UUID;

public final class TestDataGenerator {
    private TestDataGenerator() {
    }

    public static String uniqueSuffix() {
        int suffixLength = Integer.parseInt(ConfigReader.get("testdata.uniqueSuffixLength"));
        return UUID.randomUUID().toString().replace("-", "").substring(0, suffixLength);
    }

    public static String uniqueValue(String value) {
        return value + uniqueSuffix();
    }

    public static String invalidUsername() {
        return ConfigReader.get("testdata.invalidUsernamePrefix") + uniqueSuffix();
    }

    public static String invalidPassword() {
        return UUID.randomUUID().toString();
    }
}