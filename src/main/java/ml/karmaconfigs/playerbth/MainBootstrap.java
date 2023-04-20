package ml.karmaconfigs.playerbth;

import java.net.URLClassLoader;
import ml.karmaconfigs.api.common.ResourceDownloader;
import ml.karmaconfigs.api.common.karma.loader.BruteLoader;
import ml.karmaconfigs.playerbth.utils.PBTHPlugin;

public class MainBootstrap {
  private static final PBTHPlugin plugin = new PBTHPlugin();
  
  private final Main loader;
  
  public MainBootstrap(Main plugin) {
    this.loader = plugin;
  }
  
  public void enable() {
    BruteLoader appender = new BruteLoader((URLClassLoader)this.loader.getClass().getClassLoader());
    String[][] dependencies = { {
      "JodaTime.jar", "https://repo1.maven.org/maven2/joda-time/joda-time/2.10.13/joda-time-2.10.13.jar" },
            { "HikariCP.jar", "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/4.0.3/HikariCP-4.0.3.jar" }, { "Json.jar", "https://repo1.maven.org/maven2/org/json/json/20211205/json-20211205.jar" } };
    for (String[] dependency : dependencies) {
      String name = dependency[0];
      String url = dependency[1];
      ResourceDownloader downloader = ResourceDownloader.toCache(this.loader, name, url, new String[] { "dependency" });
      downloader.download();
      appender.add(downloader.getDestFile());
    } 
    plugin.initialize();
  }
  
  public void disable() {
    plugin.stop();
  }
}
