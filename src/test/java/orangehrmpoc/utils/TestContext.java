package orangehrmpoc.utils;

import org.openqa.selenium.Cookie;
import java.util.List;

public class TestContext {
    public List<Cookie> adminCookies = List.of();
    public String employeeId;
    public String employeeNumber;
    public boolean employeeCreationAttempted;
    public String firstName;
    public String updatedLastName;
}