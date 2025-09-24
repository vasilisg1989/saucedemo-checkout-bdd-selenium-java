package infiterra.pages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import infiterra.support.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
  protected final WebDriver driver;
  protected final WebDriverWait wait;
  private final long slowmo = Config.slowmoMs();
  protected final Logger log = LogManager.getLogger(getClass());
  protected BasePage(WebDriver d) {
    this.driver = d;
    this.wait = new WebDriverWait(d, Duration.ofSeconds(10));
  }

  /* ---------- Selenide-like helpers ---------- */
  protected WebElement $(By locator) {
    return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
  }

  protected List<WebElement> $$(By locator) {
    return driver.findElements(locator);
  }

  /* ---------- Common actions ---------- */
  protected void click(By locator) {
    log.debug("CLICK {}", locator);
    wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    afterActionPause();
  }

  protected void type(By locator, String text) {
    String safe = text == null ? "null" : text.replaceAll(".", "*");
    log.debug("TYPE {} = {}", locator, safe);
    WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    el.clear();
    if (text != null) el.sendKeys(text);
    afterActionPause();
  }

  protected String text(By locator) {
   String t = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    log.debug("TEXT {} -> '{}'", locator, t);
    return t;
  }

  protected boolean isDisplayed(By locator) {
    try {
      boolean shown = driver.findElement(locator).isDisplayed();
      log.trace("DISPLAYED {} -> {}", locator, shown);
      return shown;
    } catch (NoSuchElementException e) {
      log.trace("DISPLAYED {} -> false (NoSuchElement)", locator);
      return false;
    }
  }
  protected void scrollIntoView(By locator) {
    WebElement el = $(locator);
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
  }

  /* ---------- utils ---------- */
  private void afterActionPause() {
    if (slowmo > 0) {
      try { Thread.sleep(slowmo); } catch (InterruptedException ignored) {}
    }
  }

  protected WebDriver getDriver() { return driver; }
}
