package orangehrmpoc.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import orangehrmpoc.config.ConfigReader;
import org.openqa.selenium.Cookie;
import java.util.Collection;

public class ApiClient {
    private final Collection<Cookie> cookies;

    public ApiClient(Collection<Cookie> cookies) {
        this.cookies = cookies;
    }

    public RequestSpecification getAuthenticatedRequest() {

        RequestSpecification request =
                RestAssured
                        .given()
                        .baseUri(ConfigReader.get("apiBaseUrl"))
                        .header("Accept", "application/json");

        for (Cookie cookie : cookies) {

            request.cookie(
                    cookie.getName(),
                    cookie.getValue()
            );
        }

        return request;
    }
}
