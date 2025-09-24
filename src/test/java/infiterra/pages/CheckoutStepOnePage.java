package infiterra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutStepOnePage extends BasePage {

    private static final By FIRST_NAME   = By.id("first-name");
    private static final By LAST_NAME    = By.id("last-name");
    private static final By POSTAL_CODE  = By.id("postal-code");
    private static final By CONTINUE     = By.id("continue");
    private static final By ERROR_BANNER = By.cssSelector("h3[data-test='error']");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    
    public void submit(String first, String last, String postal) {
        set(FIRST_NAME, first);
        set(LAST_NAME, last);
        set(POSTAL_CODE, postal);
        click(CONTINUE);
    }

    
    private void set(By locator, String value) {
        WebElement el = $(locator);  
        el.clear();
        if (value != null && !value.isBlank()) {
            el.sendKeys(value);
        }
    }

    public String getError() {
        return isDisplayed(ERROR_BANNER) ? text(ERROR_BANNER) : "";
    }

    public boolean isLoaded() {

        return isDisplayed(CONTINUE) && driver.getCurrentUrl().contains("checkout-step-one.html");
    }
}
