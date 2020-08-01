package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.playerbth.Utils.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

public interface PlayerBTH {

    Main plugin = (Main) JavaPlugin.getProvidingPlugin(Main.class);
    String name = StringUtils.toColor("&f[ &cGSA &f] &bPlayerBTH");
    String version = StringUtils.toColor("&c" + plugin.getDescription().getVersion());

    static boolean hasNoteBlock() {
        return plugin.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI");
    }

    static String getJarName() {
        return new java.io.File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
