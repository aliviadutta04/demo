package orangehrmpoc.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import orangehrmpoc.config.ConfigReader;
import java.util.Map;

public class ApiClient {
    private final Map<String, String> cookies;

    public ApiClient(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public RequestSpecification getAuthenticatedRequest() {

        RequestSpecification request =
                RestAssured
                        .given()
                        .baseUri(ConfigReader.get("apiBaseUrl"))
                        .header("Accept", "application/json");

        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            request.cookie(cookie.getKey(), cookie.getValue());
        }

        return request;
    }
}
