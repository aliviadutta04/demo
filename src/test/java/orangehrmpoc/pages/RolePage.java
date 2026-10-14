package orangehrmpoc.pages;

import orangehrmpoc.config.ConfigReader;
import org.openqa.selenium.By;
import java.time.Duration;

public class RolePage extends BasePage {
    private static final Duration ABSENCE_TIMEOUT = Duration.ofSeconds(
            Integer.parseInt(ConfigReader.get("role.absenceTimeout")));
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
        return isAbsent(adminMenu, ABSENCE_TIMEOUT);
    }

    public boolean isPIMMenuAbsent() {
        return isAbsent(PIM_MENU, ABSENCE_TIMEOUT);
    }
}
