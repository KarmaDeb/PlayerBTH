package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.playerbth.Utils.PBTHPlugin;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable() {
        DependencyLoader jodaLoader = new DependencyLoader();
        if (jodaLoader.injectJodaTime()) {
            new PBTHPlugin().initialize();
            System.out.println("Executing PlayerBTH.jar in " + PlayerBTH.getJarName());
        } else {
            System.out.println("Couldn't inject Joda Time into PlayerBTH");
            getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        new PBTHPlugin().stop();
    }
}
