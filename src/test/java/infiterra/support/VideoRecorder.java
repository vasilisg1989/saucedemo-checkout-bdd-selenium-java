package infiterra.support;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class VideoRecorder {

  private static final ThreadLocal<NamedScreenRecorder> REC = new ThreadLocal<>();

  private static class NamedScreenRecorder extends ScreenRecorder {
    private final String prefix;

    NamedScreenRecorder(GraphicsConfiguration cfg, Rectangle area,
                        Format fileFormat, Format screenFormat,
                        Format mouseFormat, Format audioFormat,
                        File folder, String prefix)
        throws IOException, AWTException {
      super(cfg, area, fileFormat, screenFormat, mouseFormat, audioFormat, folder);
      this.prefix = prefix;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
      if (!movieFolder.exists()) movieFolder.mkdirs();
      String ext = Registry.getInstance().getExtension(fileFormat); // avi
      String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      return new File(movieFolder, prefix + "_" + ts + "." + ext);
    }
  }

  public static void start(String scenarioName) throws Exception {
    if (!Config.videoEnabled()) return;
    if (Config.headless())      return;

    File dir = new File("target/videos");
    if (!dir.exists()) dir.mkdirs();

    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration();

    String safe = scenarioName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");

    NamedScreenRecorder r = new NamedScreenRecorder(
        gc,
        new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()),
        // Αρχείο
        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
        // MJPEG = παίζει παντού
        new Format(MediaTypeKey, MediaType.VIDEO,
            EncodingKey, ENCODING_AVI_MJPG,
            CompressorNameKey, ENCODING_AVI_MJPG,
            DepthKey, 24,
            FrameRateKey, Rational.valueOf(15),
            QualityKey, 0.8f,
            KeyFrameIntervalKey, 15 * 60),
        
        null,
        null,
        dir,
        safe
    );

    REC.set(r);
    r.start();
  }

  public static File stop() throws Exception {
    NamedScreenRecorder r = REC.get();
    if (r == null) return null;

    try {
      long grace = Config.videoGraceMs();
      if (grace > 0) Thread.sleep(grace);
      Toolkit.getDefaultToolkit().sync();
      r.stop();
      List<File> files = r.getCreatedMovieFiles();
      return (files != null && !files.isEmpty()) ? files.get(0) : null;
    } finally {
      REC.remove();
    }
  }
}
