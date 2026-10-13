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
import orangehrmpoc.utils.TestContext;
import orangehrmpoc.utils.TestDataGenerator;
import io.restassured.response.Response;

public class EmployeeSteps {
    private final TestContext context;
    private DashboardPage dashboardPage;
    private String currentTestCase;
    private EmployeePage employeePage;

    public EmployeeSteps(TestContext context) {
        this.context = context;
    }

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
    context.firstName = TestDataGenerator.uniqueValue(createData.get("firstName").getAsString());
    String lastName = TestDataGenerator.uniqueValue(createData.get("lastName").getAsString());

    employeePage.openPIM();
    employeePage.openAddEmployee();

    employeePage.enterEmployeeDetails(
            context.firstName,
            createData.get("middleName").getAsString(),
            lastName);

    context.employeeId = employeePage.getEmployeeId();
    if (context.employeeId.length() > 10) {
        throw new IllegalStateException("Generated employee ID exceeds 10 characters");
    }
    employeePage.saveEmployee();
    if (!employeePage.isEmployeeCreated()) {
        throw new IllegalStateException("Employee creation did not reach the details page");
    }
            context.employeeCreationAttempted = true;
    context.employeeNumber = employeePage.getEmployeeNumberFromUrl();
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

        context.updatedLastName = TestDataGenerator.uniqueValue(updateData.get("lastName").getAsString());
        employeePage.updateLastName(context.updatedLastName);
    }

    @Then("the employee details should be updated successfully")
    public void employeeDetailsShouldBeUpdatedSuccessfully() {

        assertTrue(
                "Employee details were not updated",
                employeePage.isLastName(context.updatedLastName));
    }

    @Then("the employee role should be validated")
    public void validateEmployeeRole() {

        JsonObject employee = JsonDataReader.getEmployee(currentTestCase);

        String expectedRole = employee.get("role").getAsString();

        RolePage rolePage = new RolePage();

        assertEquals("Role has no implemented access assertions", "Admin", expectedRole);
        assertTrue("Admin role does not have access to Admin menu", rolePage.isAdminMenuDisplayed());
        assertTrue("Admin role does not have access to PIM", rolePage.isPIMDisplayed());
    }

    @Then("the ESS user should see only ESS navigation")
    public void essUserShouldSeeOnlyEssNavigation() {
        RolePage rolePage = new RolePage();
        assertTrue("ESS role should see My Info", rolePage.isMyInfoDisplayed());
        assertTrue("ESS role must not see Admin", rolePage.isAdminMenuAbsent());
        assertTrue("ESS role must not see PIM", rolePage.isPIMMenuAbsent());
    }

    @Then("the employee should be verified through API")
    public void verifyEmployeeThroughApi() {

        EmployeeApi employeeApi = new EmployeeApi(context.adminCookies);
        Response response = employeeApi.getEmployee(context.employeeNumber);

        assertEquals(
                "Unexpected API status code",
                200,
                response.statusCode());
            assertEquals("API employee number did not match", context.employeeNumber,
                response.jsonPath().getString("data.empNumber"));
            assertEquals("API first name did not match", context.firstName,
                response.jsonPath().getString("data.firstName"));
            assertEquals("API updated last name did not match", context.updatedLastName,
                response.jsonPath().getString("data.lastName"));
    }

    @When("I delete the employee")
    public void deleteEmployee() {

        employeePage.deleteEmployee(context.employeeId);
    }

    @Then("the employee should be deleted successfully")
    public void employeeShouldBeDeletedSuccessfully() {

        assertTrue(
                "Employee was not deleted successfully",
                employeePage.isEmployeeDeleted());
            context.employeeNumber = null;
            context.employeeId = null;
            context.employeeCreationAttempted = false;
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
