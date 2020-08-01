package ml.karmaconfigs.playerbth.Utils.Files;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.DataSys;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class Config implements PlayerBTH {

    private final File file = new File(plugin.getDataFolder(), "config.yml");
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public Config() {
        try {
            if (config.getInt("Ver") != 4) {
                File backupsFolder = new File(plugin.getDataFolder() + "/backups");
                if (!backupsFolder.exists()) {
                    if (backupsFolder.mkdirs()) {
                        Server.send("Created backups folder", Server.AlertLevel.INFO);
                    }
                }

                int old = 0;
                File[] files = backupsFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (name.contains("_")) {
                            if (name.split("_")[0].equals("config-old")) {
                                old++;
                            }
                        }
                    }
                }

                int amount = old + 1;
                String amountStr;
                if (amount <= 9) {
                    amountStr = "0" + amount;
                } else {
                    amountStr = String.valueOf(amount);
                }

                File newConfig = new File(plugin.getDataFolder() + "/backups", "config-old_" + amountStr + ".yml");
                String path = newConfig.getPath().replaceAll("\\\\", "/");

                if (file.renameTo(newConfig)) {
                    Server.send("Updated config.yml, have been renamed to " + path, Server.AlertLevel.WARNING);
                }

                YamlCreator creator = new YamlCreator("config.yml", true);
                creator.createFile();
                creator.setDefaults();
                creator.saveFile();

                Files.copyValues(newConfig, creator.getFile(), "Ver");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        YamlCreator creator = new YamlCreator("config.yml", true);
        creator.createFile();
        creator.setDefaults();
        creator.saveFile();
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
        System.out.println("Enable song: " + getBoolean("Song.enabled"));

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