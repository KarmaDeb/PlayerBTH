package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.playerbth.Utils.PBTHPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        new PBTHPlugin().initialize();
        System.out.println("Executing PlayerBTH.jar in " + PlayerBTH.getJarName());
    }

    @Override
    public void onDisable() {
        new PBTHPlugin().stop();
    }
}
