package ml.karmaconfigs.playerbth.Utils.Files;

import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

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

@Deprecated
public interface Files {

    Messages messages = new Messages();
    Config config = new Config();

    static void copyValues(File copyFrom, File copyTo, Object... ignore) {
        if (copyFrom.exists()) {
            try {
                if (!copyTo.exists() && copyTo.createNewFile()) {
                    Server.send("Created file {0}", Server.AlertLevel.INFO, copyTo.getAbsolutePath().replace("\\\\", "/"));
                }
                YamlConfiguration from = YamlConfiguration.loadConfiguration(copyFrom);
                YamlConfiguration to = YamlConfiguration.loadConfiguration(copyTo);

                ArrayList<String> ignores = new ArrayList<>();
                for (Object obj : ignore) {
                    ignores.add(obj.toString());
                }

                for (Object str : getPaths(copyFrom)) {
                    if (!ignores.contains(str.toString())) {
                        if (to.isSet(str.toString())) {
                            to.set(str.toString(), from.get(str.toString()));
                        }
                    }
                }

                to.save(copyTo);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static Object[] getPaths(File file) {
        YamlConfiguration read = YamlConfiguration.loadConfiguration(file);

        ArrayList<String> paths = new ArrayList<>(read.getKeys(true));

        return paths.toArray();
    }
}
