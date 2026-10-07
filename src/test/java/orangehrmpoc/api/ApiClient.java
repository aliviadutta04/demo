package orangehrmpoc.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import orangehrmpoc.config.ConfigReader;
import orangehrmpoc.driver.DriverFactory;
import org.openqa.selenium.Cookie;

public class ApiClient {
    public static RequestSpecification getAuthenticatedRequest() {

        RequestSpecification request =
                RestAssured
                        .given()
                        .baseUri(ConfigReader.get("apiBaseUrl"))
                        .header("Accept", "application/json");

        for (Cookie cookie : DriverFactory.getDriver().manage().getCookies()) {

            request.cookie(
                    cookie.getName(),
                    cookie.getValue()
            );
        }

        return request;
    }
}
