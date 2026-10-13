package orangehrmpoc.api;

import io.restassured.response.Response;
import org.openqa.selenium.Cookie;
import java.util.Collection;

public class EmployeeApi {
    private final ApiClient apiClient;

    public EmployeeApi(Collection<Cookie> cookies) {
        apiClient = new ApiClient(cookies);
    }

    public Response getEmployee(String empNumber) {
        return apiClient
                .getAuthenticatedRequest()
                .when()
                .get("/api/v2/pim/employees/" + empNumber);
    }

    public Response deleteEmployee(String empNumber) {
        return apiClient
                .getAuthenticatedRequest()
                .when()
                .delete("/api/v2/pim/employees/" + empNumber);
    }
}
