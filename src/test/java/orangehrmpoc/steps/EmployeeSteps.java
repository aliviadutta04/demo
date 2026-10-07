package orangehrmpoc.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonObject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orangehrmpoc.pages.DashboardPage;
import orangehrmpoc.pages.EmployeePage;
import orangehrmpoc.pages.RolePage;
import orangehrmpoc.utils.JsonDataReader;
import orangehrmpoc.api.EmployeeApi;
import io.restassured.response.Response;
import static org.junit.Assert.assertEquals;

public class EmployeeSteps {
    private DashboardPage dashboardPage;
    private String currentTestCase;
    private EmployeePage employeePage;
    private String employeeId;
    private String employeeNumber;

    @Then("the employee dashboard should be displayed")
    public void employeeDashboardShouldBeDisplayed() {

        dashboardPage = new DashboardPage();

        assertTrue(
                "Employee dashboard is not displayed",
                dashboardPage.isDashboardDisplayed());
    }

    @When("I create a new employee using {string} data")
public void createNewEmployee(String testCase) {

    currentTestCase = testCase;
    employeePage = new EmployeePage();

    JsonObject employee = JsonDataReader.getEmployee(testCase);

    JsonObject createData = employee.getAsJsonObject("create");

    employeePage.openPIM();
    employeePage.openAddEmployee();

    employeePage.enterEmployeeDetails(
            createData.get("firstName").getAsString(),
            createData.get("middleName").getAsString(),
            createData.get("lastName").getAsString());

    employeePage.saveEmployee();

    employeeId = employeePage.getEmployeeId();
    employeeNumber = employeePage.getEmployeeNumberFromUrl();

    System.out.println("UI Employee ID : " + employeeId);
    System.out.println("API empNumber  : " + employeeNumber);
}

    @Then("the employee should be created successfully")
    public void employeeShouldBeCreatedSuccessfully() {

        assertTrue(
                "Employee was not created successfully",
                employeePage.isEmployeeCreated());
    }

    @When("I update the employee details")
    public void updateEmployeeDetails() {

        JsonObject employee = JsonDataReader.getEmployee(currentTestCase);

        JsonObject updateData = employee.getAsJsonObject("update");

        String updatedLastName = updateData.get("lastName").getAsString();

        employeePage.updateLastName(updatedLastName);
    }

    @Then("the employee details should be updated successfully")
    public void employeeDetailsShouldBeUpdatedSuccessfully() {

        assertTrue(
                "Employee details were not updated",
                employeePage.isEmployeeUpdated());
    }

    @Then("the employee role should be validated")
    public void validateEmployeeRole() {

        JsonObject employee = JsonDataReader.getEmployee(currentTestCase);

        String expectedRole = employee.get("role").getAsString();

        RolePage rolePage = new RolePage();

        if (expectedRole.equalsIgnoreCase("Admin")) {

            assertTrue(
                    "Admin role does not have access to Admin menu",
                    rolePage.isAdminMenuDisplayed());

            assertTrue(
                    "Admin role does not have access to PIM",
                    rolePage.isPIMDisplayed());
        }
    }

    @Then("the employee should be verified through API")
    public void verifyEmployeeThroughApi() {

        System.out.println(
                "empNumber used for API: " + employeeNumber);

        EmployeeApi employeeApi = new EmployeeApi();

        Response response = employeeApi.getEmployee(employeeNumber);

        System.out.println("========== API RESPONSE ==========");
        System.out.println("Status Code : " + response.statusCode());
        System.out.println("Response    : " + response.asPrettyString());
        System.out.println("===================================");

        assertEquals(
                "Unexpected API status code",
                200,
                response.statusCode());
    }

    @When("I delete the employee")
    public void deleteEmployee() {

        employeePage.deleteEmployee(employeeId);
    }

    @Then("the employee should be deleted successfully")
    public void employeeShouldBeDeletedSuccessfully() {

        assertTrue(
                "Employee was not deleted successfully",
                employeePage.isEmployeeDeleted());
    }
   @When("I try to create an employee using {string} data")
public void tryToCreateEmployee(String testCase) {

    currentTestCase = testCase;
    employeePage = new EmployeePage();

    JsonObject employee = JsonDataReader.getEmployee(testCase);

    JsonObject createData = employee.getAsJsonObject("create");

    employeePage.openPIM();
    employeePage.openAddEmployee();

    employeePage.enterEmployeeDetails(
            createData.get("firstName").getAsString(),
            createData.get("middleName").getAsString(),
            createData.get("lastName").getAsString());

    employeePage.saveEmployee();
}
@Then("the employee should not be created")
public void employeeShouldNotBeCreated() {

    assertTrue(
            "Employee was created even though mandatory fields were missing",
            employeePage.isEmployeeValidationMessageDisplayed());
}
@Then("the employee validation message should be displayed")
public void employeeValidationMessageShouldBeDisplayed() {

    assertTrue(
            "Employee validation message was not displayed",
            employeePage.isEmployeeValidationMessageDisplayed());
}
@When("I open the add employee page")
public void openAddEmployeePage() {

    employeePage = new EmployeePage();

    employeePage.openPIM();
    employeePage.openAddEmployee();
}
@When("I click the save employee button")
public void clickSaveEmployeeButton() {

    employeePage.saveEmployee();
}


}
