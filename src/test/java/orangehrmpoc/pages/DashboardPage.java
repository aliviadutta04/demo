package orangehrmpoc.pages;

import org.openqa.selenium.By;

public class DashboardPage extends BasePage {
    private By dashboardHeader =
            By.xpath("//h6[normalize-space()='Dashboard']");

    public boolean isDashboardDisplayed() {
        return isDisplayed(dashboardHeader);
    }
}
