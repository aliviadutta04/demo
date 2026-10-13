package orangehrmpoc.pages;

import orangehrmpoc.config.ConfigReader;
import org.openqa.selenium.By;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeePage extends BasePage {
    private By firstName = By.name("firstName");

    private By middleName = By.name("middleName");

    private By lastName = By.name("lastName");

    private By saveButton = By.cssSelector("button[type='submit']");
    private By updateSaveButton = By.xpath("//div[@class='orangehrm-horizontal-padding orangehrm-vertical-padding']//button[@type='submit'][normalize-space()='Save']");
    private By employeeDetailsHeader = By.xpath("//h6[normalize-space()='Personal Details']");

    private By apiEmployeeId = By
            .xpath("//label[normalize-space()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private By employeeIdSearch = By.xpath("//label[normalize-space()='Employee Id']/../following-sibling::div//input");

    private By searchButton = By.xpath("//button[normalize-space()='Search']");

    //private By employeeCheckbox = By.xpath("//div[@role='row']//input[@type='checkbox']");
    // private By employeeCheckbox = By.xpath("//div[@class='oxd-table-card-cell-checkbox']//i[@class='oxd-icon bi-check oxd-checkbox-input-icon']");

    // private By deleteButton = By.xpath("//button[contains(@class,'oxd-button') and normalize-space()='Delete']");

            private By confirmDeleteButton = By.cssSelector(
                ".oxd-dialog-container-default button.oxd-button--label-danger");

    private By noRecordsMessage = By.xpath("//span[contains(normalize-space(),'No Records Found')]");
//     @FindBy(xpath = "//span[contains(@class,'oxd-input-field-error-message')]")
// private List<WebElement> validationMessages;
private By validationMessage =
        By.xpath("//span[contains(@class,'oxd-input-field-error-message')]");
    public void openPIM() {
        click(PIM_MENU);
    }

    public void openAddEmployee() {
        navigateTo(ConfigReader.get("apiBaseUrl") + "/pim/addEmployee");
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

    public boolean isEmployeeCreated() {
        return isDisplayed(employeeDetailsHeader);
    }

    public void updateLastName(String value) {
        waitForFormLoaderToDisappear();
        enterText(lastName, value);
        clickWithJavascriptFallback(updateSaveButton);
    }

    public boolean isLastName(String expectedLastName) {
        return hasAttributeValue(lastName, "value", expectedLastName);
    }

    public String getEmployeeId() {
        return getAttribute(apiEmployeeId, "value");
    }

    public void openEmployeeList() {
        navigateTo(ConfigReader.get("apiBaseUrl") + "/pim/viewEmployeeList");
    }

    public void searchEmployee(String employeeIdValue) {

        enterText(employeeIdSearch, employeeIdValue);

        click(searchButton);
    }

    public void deleteEmployee(String employeeIdValue) {

        openEmployeeList();

        searchEmployee(employeeIdValue);

        String employeeRow = "//div[@role='row'][.//div[normalize-space()='" + employeeIdValue + "']]";
        click(By.xpath(employeeRow + "//input[@type='checkbox']/following-sibling::*[1]"));

        click(By.xpath(employeeRow
            + "//button[contains(@class,'oxd-icon-button')][.//i[contains(@class,'bi-trash')]]"));

        clickWithJavascriptFallback(confirmDeleteButton);
        waitUntilInvisible(By.xpath(employeeRow));
    }

    public boolean isEmployeeDeleted() {

        return isDisplayed(noRecordsMessage);
    }
    public String getEmployeeNumberFromUrl() {

        String currentUrl = waitForUrlContains("/empNumber/");

    Pattern pattern = Pattern.compile("/empNumber/(\\d+)");
    Matcher matcher = pattern.matcher(currentUrl);

    if (matcher.find()) {
        return matcher.group(1);
    }

    throw new RuntimeException(
            "Unable to extract empNumber from URL: " + currentUrl
    );
}
public boolean isEmployeeValidationMessageDisplayed() {

    return isDisplayed(validationMessage);
}
}
