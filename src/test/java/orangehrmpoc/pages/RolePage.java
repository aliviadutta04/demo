package orangehrmpoc.pages;

import org.openqa.selenium.By;
import java.time.Duration;

public class RolePage extends BasePage {
    private By adminMenu = By.cssSelector("a[href*='/admin/viewAdminModule']");
    private By myInfoMenu = By.cssSelector("a[href*='/pim/viewMyDetails']");

    public boolean isAdminMenuDisplayed() {
        return isDisplayed(adminMenu);
    }

    public boolean isPIMDisplayed() {
        return isDisplayed(PIM_MENU);
    }

    public boolean isMyInfoDisplayed() {
        return isDisplayed(myInfoMenu);
    }

    public boolean isAdminMenuAbsent() {
        return isAbsent(adminMenu, Duration.ofSeconds(2));
    }

    public boolean isPIMMenuAbsent() {
        return isAbsent(PIM_MENU, Duration.ofSeconds(2));
    }
}
