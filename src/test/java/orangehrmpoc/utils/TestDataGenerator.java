package orangehrmpoc.utils;

import java.util.UUID;

public final class TestDataGenerator {
    private TestDataGenerator() {
    }

    public static String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String uniqueValue(String value) {
        return value + uniqueSuffix();
    }

    public static String invalidUsername() {
        return "unknown_" + uniqueSuffix();
    }

    public static String invalidPassword() {
        return UUID.randomUUID().toString();
    }
}