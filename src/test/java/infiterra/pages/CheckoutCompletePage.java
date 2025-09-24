package infiterra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutCompletePage extends BasePage {

    private final By completeContainer = By.id("checkout_complete_container");
    private final By header = By.cssSelector("[data-test='complete-header']");
    private final By text   = By.cssSelector("[data-test='complete-text']");
    private final By backHome = By.cssSelector("[data-test='back-to-products']");

    public CheckoutCompletePage(WebDriver driver) { super(driver); }

    public boolean isLoaded() { return isDisplayed(completeContainer); }

    public String headerText() { return text(header); }

    public String bodyText() { return text(text); }

    public void backHome() { click(backHome); }
}
