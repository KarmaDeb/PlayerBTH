package ml.karmaconfigs.playerbth.utils.files;

import ml.karmaconfigs.api.bukkit.karmayaml.FileCopy;
import ml.karmaconfigs.api.bukkit.karmayaml.YamlReloader;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.DataSys;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

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

public final class Config implements PlayerBTH {

    private final static File file = new File(plugin.getDataFolder(), "config.yml");
    private final static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public interface manager {

        static boolean reload() {
            try {
                YamlReloader reloader = new YamlReloader(plugin, file, "config.yml");
                if (reloader.reloadAndCopy()) {
                    config.loadFromString(reloader.getYamlString());
                    return true;
                }
            } catch (Throwable ex) {
                try {
                    FileCopy copy = new FileCopy(plugin, "config.yml");
                    copy.copy(file);
                    return true;
                } catch (Throwable ignored) {}
            }
            return false;
        }
    }

    private boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    private int getInt(String path) {
        return config.getInt(path);
    }

    private String getString(String path) {
        return config.getString(path);
    }

    public final boolean enableSong() {
        return getBoolean("Song.enabled");
    }

    public final String getSong() {
        return getString("Song.Name");
    }

    public final boolean enableFireWorks() {
        return getBoolean("Firework.enabled");
    }

    public final int fireworkPower() {
        return getInt("Firework.power");
    }

    public final int fireworkAmount() {
        return getInt("Firework.amount");
    }

    public final boolean giveCake() {
        return getBoolean("Cake");
    }

    public final DataSys getDataSystem() {
        try {
            return DataSys.valueOf(getString("DataSys").toUpperCase());
        } catch (Throwable e) {
            return DataSys.FILE;
        }
    }

    public final boolean notifyUpdated() {
        return getBoolean("Update.NotifyUpdated");
    }

    public final boolean sendChangeLogs() {
        return getBoolean("Update.Changelog");
    }

    public final long checkInterval() {
        return TimeUnit.MINUTES.toSeconds(getInt("Update.Interval"));
    }

    public final boolean downloadToUpdate() {
        return getBoolean("Update.UpdateFolder");
    }

    public final String birthdayNotSet() {
        return getString("BdNotSet");
    }

    public final String birthdaySet() {
        return getString("BdSet");
    }

    public final String mysqlHost() {
        return getString("MySQL.Host");
    }

    public final int mysqlPort() {
        return getInt("MySQL.Port");
    }

    public final String mysqlUser() {
        return getString("MySQL.User");
    }

    public final String mysqlPassword() {
        return getString("MySQL.Password");
    }

    public final String mysqlDatabase() {
        return getString("MySQL.Database");
    }

    public final String mysqlTable() {
        return getString("MySQL.Table");
    }

    public final boolean useSSL() {
        return getBoolean("MySQL.SSL");
    }

    public final int getMinConnections() {
        return getInt("MySQL.Connection.Min");
    }

    public final int getMaxConnections() {
        return getInt("MySQL.Connection.Max");
    }

    public final int getConnectionTimeOut() {
        return getInt("MySQL.Connection.TimeOut");
    }

    public final int getConnectionLifeTime() {
        return getInt("MySQL.Connection.LifeTime");
    }
}