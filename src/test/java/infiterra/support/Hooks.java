package infiterra.support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.io.File;
import java.nio.charset.StandardCharsets;


public class Hooks {
  private static final Logger log = LogManager.getLogger(Hooks.class);

  @Before
  public void before(Scenario sc) {
    log.info("=== START Scenario: {} ===", sc.getName());
    try {
      DriverFactory.initDriver();           
      VideoRecorder.start(sc.getName());    
      log.info("Video recording started");
    } catch (Exception e) {
      log.error("Failed to start video recording", e);
    }
  }

  @After
  public void after(Scenario sc) {
    try {
      if (sc.isFailed()) {
        try {
          TakesScreenshot ts = (TakesScreenshot) DriverFactory.getDriver();
          byte[] png = ts.getScreenshotAs(OutputType.BYTES);
          sc.attach(png, "image/png", "failure-screenshot");
          log.info("Failure screenshot captured");
        } catch (Exception e) {
          log.warn("Could not capture screenshot", e);
        }
      }

      File video = VideoRecorder.stop();
      if (video != null && video.exists()) {
        log.info("Video saved: {}", video.getAbsolutePath());
        sc.attach(("Video: " + video.getAbsolutePath()).getBytes(StandardCharsets.UTF_8),
                  "text/plain", "video-path");
      } else {
        log.warn("No video file produced");
      }
    } catch (Exception e) {
      log.error("Failed to stop video recording", e);
    } finally {
      DriverFactory.quitDriver();
      log.info("=== END Scenario: {} (status: {}) ===", sc.getName(), sc.getStatus());
    }
  }
}
