package orangehrmpoc.api;

import io.restassured.response.Response;

public class EmployeeApi {
    public Response getEmployee(String empNumber) {

        Response response = ApiClient
                .getAuthenticatedRequest()
                .when()
                .get("/api/v2/pim/employees/" + empNumber);

        System.out.println("========== API RESPONSE ==========");
        System.out.println("Status Code : " + response.statusCode());
        System.out.println("Response    : " + response.asPrettyString());
        System.out.println("===================================");

        return response;
    }
}
