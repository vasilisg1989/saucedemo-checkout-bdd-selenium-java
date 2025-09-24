package infiterra.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverFactory {
  private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

  public static void initDriver() {
    if (TL.get() != null) return;

    WebDriverManager.chromedriver().setup();
    ChromeOptions opts = new ChromeOptions();
    if (Config.headless()) opts.addArguments("--headless=new");
    if (Config.incognito()) opts.addArguments("--incognito");
    opts.addArguments("--disable-infobars", "--disable-notifications");
    TL.set(new ChromeDriver(opts));

    WebDriver d = TL.get();
    d.manage().timeouts().implicitlyWait(Duration.ZERO);
    d.manage().window().maximize();
  }

  public static WebDriver getDriver() { return TL.get(); }

  public static void quitDriver() {
    WebDriver d = TL.get();
    if (d != null) {
      d.quit();
      TL.remove();
    }
  }
}
