package orangehrmpoc.steps;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.google.gson.JsonObject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import orangehrmpoc.pages.DashboardPage;
import orangehrmpoc.pages.EmployeePage;
import orangehrmpoc.pages.RolePage;
import orangehrmpoc.config.ConfigReader;
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

        assertTrue(dashboardPage.isDashboardDisplayed(), "Employee dashboard is not displayed");
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
    int maximumEmployeeIdLength = Integer.parseInt(ConfigReader.get("employee.id.maxLength"));
    if (context.employeeId.length() > maximumEmployeeIdLength) {
        throw new IllegalStateException(
                "Generated employee ID exceeds " + maximumEmployeeIdLength + " characters");
    }
    context.employeeCreationAttempted = true;
    employeePage.saveEmployee();
    if (!employeePage.isEmployeeCreated()) {
        throw new IllegalStateException("Employee creation did not reach the details page");
    }
    context.employeeNumber = employeePage.getEmployeeNumberFromUrl();
}

    @Then("the employee should be created successfully")
    public void employeeShouldBeCreatedSuccessfully() {

        assertTrue(employeePage.isEmployeeCreated(), "Employee was not created successfully");
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

        assertTrue(employeePage.isLastName(context.updatedLastName), "Employee details were not updated");
    }

    @Then("the configured Admin user should see Admin and PIM navigation")
    public void configuredAdminUserShouldSeeAdminAndPimNavigation() {
        RolePage rolePage = new RolePage();
        assertTrue(rolePage.isAdminMenuDisplayed(), "Admin role does not have access to Admin menu");
        assertTrue(rolePage.isPIMDisplayed(), "Admin role does not have access to PIM");
    }

    @Then("the ESS user should see only ESS navigation")
    public void essUserShouldSeeOnlyEssNavigation() {
        RolePage rolePage = new RolePage();
        assertTrue(rolePage.isMyInfoDisplayed(), "ESS role should see My Info");
        assertTrue(rolePage.isAdminMenuAbsent(), "ESS role must not see Admin");
        assertTrue(rolePage.isPIMMenuAbsent(), "ESS role must not see PIM");
    }

    @Then("the employee should be verified through API")
    public void verifyEmployeeThroughApi() {

        EmployeeApi employeeApi = new EmployeeApi(context.adminCookies);
        Response response = employeeApi.getEmployee(context.employeeNumber);

        assertEquals(response.statusCode(),
            Integer.parseInt(ConfigReader.get("employee.api.successStatus")),
            "Unexpected API status code");
        assertEquals(response.jsonPath().getString("data.empNumber"), context.employeeNumber,
            "API employee number did not match");
        assertEquals(response.jsonPath().getString("data.firstName"), context.firstName,
            "API first name did not match");
        assertEquals(response.jsonPath().getString("data.lastName"), context.updatedLastName,
            "API updated last name did not match");
    }

    @When("I delete the employee")
    public void deleteEmployee() {

        employeePage.deleteEmployee(context.employeeId);
    }

    @Then("the employee should be deleted successfully")
    public void employeeShouldBeDeletedSuccessfully() {

        assertTrue(employeePage.isEmployeeDeleted(), "Employee was not deleted successfully");
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

        assertTrue(employeePage.isEmployeeValidationMessageDisplayed(),
            "Employee was created even though mandatory fields were missing");
}
@Then("the employee validation message should be displayed")
public void employeeValidationMessageShouldBeDisplayed() {

        assertTrue(employeePage.isEmployeeValidationMessageDisplayed(),
            "Employee validation message was not displayed");
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
