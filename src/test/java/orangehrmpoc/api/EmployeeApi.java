package orangehrmpoc.api;

import io.restassured.response.Response;
import orangehrmpoc.config.ConfigReader;
import java.util.Map;

public class EmployeeApi {
    private final ApiClient apiClient;

    public EmployeeApi(Map<String, String> cookies) {
        apiClient = new ApiClient(cookies);
    }

    public Response getEmployee(String empNumber) {
        return apiClient
                .getAuthenticatedRequest()
                .when()
                .get(ConfigReader.get("employee.api.endpoint") + "/" + empNumber);
    }

    public Response deleteEmployee(String empNumber) {
        return apiClient
                .getAuthenticatedRequest()
                .when()
                .delete(ConfigReader.get("employee.api.endpoint") + "/" + empNumber);
    }
}
