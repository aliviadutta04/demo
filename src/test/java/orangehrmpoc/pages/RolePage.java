package orangehrmpoc.pages;

import org.openqa.selenium.By;

public class RolePage extends BasePage {
       private By adminMenu =
            By.xpath("//span[normalize-space()='Admin']");

    private By pimMenu =
            By.xpath("//span[normalize-space()='PIM']");

    public boolean isAdminMenuDisplayed() {
        return isDisplayed(adminMenu);
    }

    public boolean isPIMDisplayed() {
        return isDisplayed(pimMenu);
    }
}
