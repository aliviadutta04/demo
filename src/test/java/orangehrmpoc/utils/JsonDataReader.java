package orangehrmpoc.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonDataReader {
    private static final String FILE_PATH =
            "/testdata/employeeData.json";

    private static JsonObject getJsonObject() {

        InputStream inputStream =
                JsonDataReader.class
                        .getResourceAsStream(FILE_PATH);

        if (inputStream == null) {
            throw new RuntimeException(
                    "Test data file not found: " + FILE_PATH);
        }

        try (InputStreamReader reader =
                     new InputStreamReader(
                             inputStream,
                             StandardCharsets.UTF_8)) {

            return JsonParser.parseReader(reader)
                    .getAsJsonObject();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read test data file", e);
        }
    }

    public static JsonObject getEmployee(String testCase) {

        JsonObject root = getJsonObject();

        for (var employee :
                root.getAsJsonArray("employees")) {

            JsonObject employeeObject =
                    employee.getAsJsonObject();

            if (employeeObject
                    .get("testCase")
                    .getAsString()
                    .equalsIgnoreCase(testCase)) {

                return employeeObject;
            }
        }

        throw new RuntimeException(
                "Employee test data not found: "
                        + testCase);
    }
}
