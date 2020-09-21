package ml.karmaconfigs.playerbth.Utils;

import ml.karmaconfigs.API.Spigot.KarmaYaml.FileCopy;
import ml.karmaconfigs.playerbth.Commands.StaffCommands;
import ml.karmaconfigs.playerbth.Commands.UserCommands;
import ml.karmaconfigs.playerbth.Events.PlayerJoinEvent;
import ml.karmaconfigs.playerbth.Metrics.Metrics;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.Birthday.Month;
import ml.karmaconfigs.playerbth.Utils.Files.Files;
import ml.karmaconfigs.playerbth.Utils.Files.YamlCreator;
import ml.karmaconfigs.playerbth.Utils.MySQL.SQLPool;
import ml.karmaconfigs.playerbth.Version.UpdaterFunction;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

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

public class PBTHPlugin implements PlayerBTH, Files {

    /**
     * Initialize the plugin
     */
    public final void initialize() {
        Server.send("&b-------------------");
        Server.send(" ");
        Server.send("&aInitializing {0} &aversion {1}", Server.name, Server.version);
        Server.send(" ");
        Server.send("&b-------------------");
        initFiles();

        Objects.requireNonNull(plugin.getCommand("bth")).setExecutor(new UserCommands());
        Objects.requireNonNull(plugin.getCommand("bths")).setExecutor(new StaffCommands());

        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), plugin);
        onInitialize();

        if (!plugin.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            Server.send("NoteBlockAPI not found, we recommend you to download it if you want to play songs", Server.AlertLevel.WARNING);
        } else {
            Server.send("NoteBlockAPI found, hooking it", Server.AlertLevel.INFO);
        }

        startChecking();
        doMetrics();
    }

    /**
     * Stop the plugin
     */
    public final void stop() {
        Server.send("&b-------------------");
        Server.send(" ");
        Server.send("&cStopping {0}", Server.name, Server.version);
        Server.send(" ");
        Server.send("&b-------------------");
        SQLPool.terminateMySQL();
        onStop();
    }

    /**
     * <code>This can be used as API</code>
     * What the plugin will do on initialize
     */
    public void onInitialize() {}

    /**
     * <code>This can be used as API</code>
     * What the plugin will do on stop
     */
    public void onStop() {}

    /**
     * Initialize the files
     */
    private void initFiles() {
        File config_yml = new File(plugin.getDataFolder(), "config.yml");
        File messages_yml = new File(plugin.getDataFolder(), "messages.yml");

        FileCopy config_copy = new FileCopy(plugin, "config.yml");
        config_copy.setDirCreatedMessage("&7[ &bPlayerBTH &7] &7INFO: &bCreated directory {path}");
        config_copy.setFileCreatedMessage("&7[ &bPlayerBTH &7] &7INFO: &bCreated file {path}");
        FileCopy messages_copy = new FileCopy(plugin, "messages.yml");
        if (config_copy.copy(config_yml)) {
            Server.send("Copied config.yml file and comments", Server.AlertLevel.INFO);
        }
        if (messages_copy.copy(messages_yml)) {
            Server.send("Copied messages.yml file and comments", Server.AlertLevel.INFO);
        }
        YamlCreator creator = new YamlCreator("commands.yml", "commands.yml");
        creator.createFile();
        creator.setDefaults();
        creator.saveFile();

        File oldSongsFolder = new File(plugin.getDataFolder() + "/Songs");
        File newSongsFolder = new File(plugin.getDataFolder() + "/songs");
        if (oldSongsFolder.exists()) {
            if (!newSongsFolder.exists()) {
                if (newSongsFolder.mkdirs()) {
                    if (oldSongsFolder.renameTo(newSongsFolder)) {
                        Server.send("Changed old songs folder (Songs) to new one (songs)", Server.AlertLevel.INFO);
                    }
                }
            }
        }

        File[] playersDat = new File(plugin.getDataFolder() + "/Users").listFiles();
        File oldPlayersDat = new File(plugin.getDataFolder() + "/Users");
        File newPlayersDat = new File(plugin.getDataFolder() + "/users");
        if (playersDat != null) {
            HashMap<File, Boolean> conversion = new HashMap<>();
            long start = System.nanoTime();
            for (File file : playersDat) {
                if (getExtension(file).equals("yml")) {
                    try {
                        String path = file.getPath().replaceAll("\\\\", "/");

                        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                        UUID uuid = UUID.fromString(Objects.requireNonNull(playerFile.getString("UUID")));
                        OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);

                        User user = new User(player);

                        int day = Integer.parseInt(Objects.requireNonNull(playerFile.getString("Birthday.Date")).split("/")[0]);
                        int month = Integer.parseInt(Objects.requireNonNull(playerFile.getString("Birthday.Date")).split("/")[1]);
                        Birthday birthday = new Birthday(Month.byID(month), day);
                        birthday.setAge(playerFile.getInt("Birthday.Age"));

                        user.setBirthday(birthday);
                        user.setNotifications(playerFile.getBoolean("Birthday.Public"));

                        if (file.delete()) {
                            Server.send("Conversion of player file {0} to new player file complete", Server.AlertLevel.INFO, path);
                        } else {
                            Server.send("Failed to delete old player file {0} and will be removed on exit", Server.AlertLevel.ERROR, path);
                            file.deleteOnExit();
                        }
                        conversion.put(file, true);
                    } catch (Throwable e) {
                        Server.log(e);
                        conversion.put(file, false);
                    }
                }
            }
            if (!conversion.isEmpty()) {
                float elapsedTime = (System.nanoTime() - start) / 1000F;
                if (oldPlayersDat.renameTo(newPlayersDat)) {
                    Server.send("Old player data conversion finisihed with a total of {0} errors, {1} success and took {2} ms", Server.AlertLevel.INFO, getErrors(conversion), getSuccess(conversion), elapsedTime);
                }
            }
        }

        if (config.getDataSystem().equals(DataSys.MYSQL)) {
            SQLPool pool = new SQLPool(config.mysqlHost(), config.mysqlDatabase(), config.mysqlTable(), config.mysqlUser(), config.mysqlPassword(), config.mysqlPort(), config.useSSL());
            pool.setOptions(config.getMaxConnections(), config.getMinConnections(), config.getConnectionTimeOut(), config.getConnectionLifeTime());

            pool.prepareTables();
        }
    }

    /**
     * Initialize the version checker
     */
    private void startChecking() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            UpdaterFunction function = new UpdaterFunction();
            function.checkVersion();
        }, 0, 20 * Files.config.checkInterval());
    }

    /**
     * Initialize the metrics class
     */
    private void doMetrics() {
        Metrics metrics = new Metrics(plugin, 6068);

        metrics.getPluginData();
    }

    private int getErrors(HashMap<File, Boolean> map) {
        int errors = 0;
        for (Boolean bool : map.values()) {
            if (bool.equals(false)) {
                errors++;
            }
        }

        return errors;
    }

    private int getSuccess(HashMap<File, Boolean> map) {
        int success = 0;
        for (Boolean bool : map.values()) {
            if (bool.equals(true)) {
                success++;
            }
        }

        return success;
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        String[] dots = fileName.split("\\.");
        return dots[dots.length - 1];
    }
}
