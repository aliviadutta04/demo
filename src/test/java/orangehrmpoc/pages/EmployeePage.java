package orangehrmpoc.pages;

import org.openqa.selenium.By;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeePage extends BasePage {
    private By pimMenu = By.xpath("//span[normalize-space()='PIM']");

    private By addEmployee = By.xpath("//a[normalize-space()='Add Employee']");

    private By firstName = By.name("firstName");

    private By middleName = By.name("middleName");

    private By lastName = By.name("lastName");

    private By employeeId = By.xpath("//label[normalize-space()='Employee Id']/../following-sibling::div/input");

    private By saveButton = By.xpath("//button[normalize-space()='Save']");
    private By updateSaveButton = By.xpath("//div[@class='orangehrm-horizontal-padding orangehrm-vertical-padding']//button[@type='submit'][normalize-space()='Save']");
    private By employeeDetailsHeader = By.xpath("//h6[normalize-space()='Personal Details']");

    private By apiEmployeeId = By
            .xpath("//label[normalize-space()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private By employeeList = By.xpath("//a[normalize-space()='Employee List']");

    private By employeeIdSearch = By.xpath("//label[normalize-space()='Employee Id']/../following-sibling::div//input");

    private By searchButton = By.xpath("//button[normalize-space()='Search']");

    //private By employeeCheckbox = By.xpath("//div[@role='row']//input[@type='checkbox']");
    private By employeeCheckbox = By.xpath("//div[@class='oxd-table-card-cell-checkbox']//i[@class='oxd-icon bi-check oxd-checkbox-input-icon']");

    private By deleteButton = By.xpath("//button[contains(@class,'oxd-button') and normalize-space()='Delete']");

    private By confirmDeleteButton = By.xpath("//button[normalize-space()='Yes, Delete']");

    private By noRecordsMessage = By.xpath("//span[contains(normalize-space(),'No Records Found')]");


    public void openPIM() {
        click(pimMenu);
    }

    public void openAddEmployee() {
        click(addEmployee);
    }

    public void enterEmployeeDetails(
            String firstNameValue,
            String middleNameValue,
            String lastNameValue) {

        enterText(firstName, firstNameValue);
        enterText(middleName, middleNameValue);
        enterText(lastName, lastNameValue);
    }

    public void saveEmployee() {
        click(saveButton);
    }
    // public void updateSaveEmployee() {
    //     click(updateSaveButton);
    // }

    public boolean isEmployeeCreated() {
        return isDisplayed(employeeDetailsHeader);
    }

    public void updateLastName(String value) {
        enterText(lastName, value);
        click(updateSaveButton);
    }

    public boolean isEmployeeUpdated() {
        return isDisplayed(employeeDetailsHeader);
    }

    public String getEmployeeId() {

        return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .visibilityOfElementLocated(apiEmployeeId))
                .getAttribute("value");
    }

    public void openEmployeeList() {

        click(employeeList);
    }

    public void searchEmployee(String employeeIdValue) {

        enterText(employeeIdSearch, employeeIdValue);

        click(searchButton);
    }

    public void deleteEmployee(String employeeIdValue) {

        openEmployeeList();

        searchEmployee(employeeIdValue);

        click(employeeCheckbox);

        click(deleteButton);

        click(confirmDeleteButton);
    }

    public boolean isEmployeeDeleted() {

        return isDisplayed(noRecordsMessage);
    }
    public String getEmployeeNumberFromUrl() {

    wait.until(driver ->
            driver.getCurrentUrl().contains("/empNumber/")
    );

    String currentUrl = driver.getCurrentUrl();

    Pattern pattern = Pattern.compile("/empNumber/(\\d+)");
    Matcher matcher = pattern.matcher(currentUrl);

    if (matcher.find()) {
        return matcher.group(1);
    }

    throw new RuntimeException(
            "Unable to extract empNumber from URL: " + currentUrl
    );
}
}
