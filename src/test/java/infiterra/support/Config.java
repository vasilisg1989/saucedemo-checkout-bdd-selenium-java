package infiterra.support;

import java.io.InputStream;
import java.util.Properties;

public final class Config {
  private static final Properties P = new Properties();
private Config() {}
  static {
    
    load("cucumber.properties"); 
    load("config.properties");  
  }

  private static void load(String name) {
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream(name)) {
      if (in != null) {
        P.load(in);
      }
    } catch (Exception ignored) {}
  }

  private static boolean blank(String s) { return s == null || s.isBlank(); }

  private static String envName(String key) {
    return key.toUpperCase().replace('.', '_');
  }

  private static String fromSystemOrEnv(String key) {
    String v = System.getProperty(key);
    if (!blank(v)) return v;
    v = System.getenv(envName(key));
    return blank(v) ? null : v;
  }

  
  public static String get(String key, String def) {
    String v = fromSystemOrEnv(key);
    if (blank(v)) v = P.getProperty(key);
    return blank(v) ? def : v.trim();
  }

  public static boolean getBoolean(String key, boolean def) {
    String v = get(key, null);
    if (blank(v)) return def;
    return v.equalsIgnoreCase("true") || v.equalsIgnoreCase("1") || v.equalsIgnoreCase("yes");
  }

  public static long getLong(String key, long def) {
    String v = get(key, null);
    if (blank(v)) return def;
    try { return Long.parseLong(v.trim()); } catch (Exception e) { return def; }
  }

  public static String swagUser()    { return get("swag.user", "standard_user"); }
  public static String swagPass()    { return get("swag.pass", "secret_sauce"); }

  public static boolean headless()     { return getBoolean("headless", false); }
  public static boolean incognito()    { return getBoolean("incognito", true); }
  public static boolean videoEnabled() { return getBoolean("video.enabled", true); }
  public static long videoGraceMs()    { return getLong("video.grace.ms", 800); }
  public static long slowmoMs()        { return getLong("slowmo.ms", 200); }
}
