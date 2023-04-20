package ml.karmaconfigs.playerbth;

import java.io.File;

import ml.karmaconfigs.api.common.console.Console;
import ml.karmaconfigs.api.common.logger.KarmaLogger;
import ml.karmaconfigs.api.common.string.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

public interface PlayerBTH {
  public static final Main plugin = (Main)JavaPlugin.getProvidingPlugin(Main.class);
  
  public static final String name = StringUtils.toColor("&f[ &cGSA &f] &bPlayerBTH");
  
  public static final String version = StringUtils.toColor("&c" + plugin.getDescription().getVersion());
  
  public static final KarmaLogger logger = plugin.logger();
  
  public static final Console console = plugin.console();
  
  static boolean hasNoteBlock() {
    return plugin.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI");
  }
  
  static String getJarName() {
    return (new File(Main.class.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .getPath()))
      .getName();
  }
}
