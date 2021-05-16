package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.api.bukkit.Console;
import ml.karmaconfigs.api.common.JarInjector;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.playerbth.utils.PBTHPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/*
GNU LESSER GENERAL PUBLIC LICENSE
                       Version 2.1, February 1999
 Copyright (C) 1991, 1999 Free Software Foundation, Inc.
 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
[This is the first released version of the Lesser GPL.  It also counts
 as the successor of the GNU Library Public License, version 2, hence
 the version number 2.1.]
 */

public final class Main extends JavaPlugin {

    private final static PBTHPlugin plugin = new PBTHPlugin();

    @Override
    public void onEnable() {
        File dependency = new File(getDataFolder() + "/libs/", "JodaTimeV21010.jar");
        try {
            JarInjector injector = new JarInjector(dependency);
            if (!dependency.exists())
                injector.download("https://github.com/JodaOrg/joda-time/releases/download/v2.10.10/joda-time-2.10.10.jar");

            if (injector.inject(JavaPlugin.getProvidingPlugin(Main.class))) {
                plugin.initialize();
            } else {
                Console.send(this, "Failed to inject JodaTime dependency", Level.GRAVE);
                getPluginLoader().disablePlugin(this);
            }
        } catch (Throwable ex) {
            Console.send(this, "Failed to inject JodaTime dependency", Level.GRAVE);
            getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        plugin.stop();
    }
}
