package ml.karmaconfigs.playerbth.utils;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.karmafile.karmayaml.FileCopy;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.playerbth.commands.StaffCommands;
import ml.karmaconfigs.playerbth.commands.UserCommands;
import ml.karmaconfigs.playerbth.events.PlayerJoinEvent;
import ml.karmaconfigs.playerbth.metrics.Metrics;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.birthday.PlayerBTHExpansion;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.mysql.SQLPool;
import ml.karmaconfigs.playerbth.version.UpdaterFunction;
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
        Console.send("&b-------------------");
        Console.send(" ");
        Console.send("&aInitializing {0} &aversion {1}", name, version);
        Console.send(" ");
        Console.send("&b-------------------");
        initFiles();

        Objects.requireNonNull(plugin.getCommand("bth")).setExecutor(new UserCommands());
        Objects.requireNonNull(plugin.getCommand("bths")).setExecutor(new StaffCommands());

        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), plugin);
        onInitialize();

        if (!plugin.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            Console.send(plugin, "NoteBlockAPI not found, we recommend you to download it if you want to play songs", Level.WARNING);
        } else {
            Console.send(plugin, "NoteBlockAPI found, hooking it", Level.OK);
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Console.send(plugin, "PlaceholderAPI not found, we recommend you to use it as it may provide util information", Level.WARNING);
        } else {
            Console.send(plugin, "PlaceholderAPI found, hooking it", Level.OK);
            PlayerBTHExpansion expansion = new PlayerBTHExpansion();
            expansion.register();
        }

        startChecking();
        doMetrics();
    }

    /**
     * Stop the plugin
     */
    public final void stop() {
        Console.send("&b-------------------");
        Console.send(" ");
        Console.send("&cStopping {0}", name, version);
        Console.send(" ");
        Console.send("&b-------------------");
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
        FileCopy messages_copy = new FileCopy(plugin, "messages.yml");

        try {
            config_copy.copy(config_yml);
            messages_copy.copy(messages_yml);
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Failed while setting up files ( config.yml | messages.yml )");
            Console.send(plugin, "An error occurred while setting up files, check plugin logs for more info", Level.GRAVE);
        }

        try {
            File commands_yml = new File(plugin.getDataFolder(), "commands.yml");
            FileCopy creator = new FileCopy(plugin, "commands.yml");
            creator.copy(commands_yml);
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Failed to check file commands.yml");
        }

        File oldSongsFolder = new File(plugin.getDataFolder() + "/Songs");
        File newSongsFolder = new File(plugin.getDataFolder() + "/songs");
        if (oldSongsFolder.exists()) {
            if (!newSongsFolder.exists()) {
                if (newSongsFolder.mkdirs()) {
                    if (oldSongsFolder.renameTo(newSongsFolder)) {
                        Console.send(plugin, "Changed old songs folder (Songs) to new one (songs)", Level.INFO);
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
                            Console.send(plugin, "Conversion of player file {0} to new player file complete", Level.OK, FileUtilities.getPrettyPath(file));
                        } else {
                            Console.send(plugin, "Failed to delete old player file {0} and will be removed on exit", Level.WARNING, FileUtilities.getPrettyPath(file));
                            file.deleteOnExit();
                        }
                        conversion.put(file, true);
                    } catch (Throwable ex) {
                        logger.scheduleLog(Level.GRAVE, ex);
                        logger.scheduleLog(Level.INFO, "Failed to converse old player file {0} to new player file", FileUtilities.getPrettyPath(file));
                        conversion.put(file, false);
                    }
                }
            }
            if (!conversion.isEmpty()) {
                float elapsedTime = (System.nanoTime() - start) / 1000F;
                if (oldPlayersDat.renameTo(newPlayersDat)) {
                    Console.send("Old player data conversion finisihed with a total of {0} errors, {1} success and took {2} ms", Level.INFO, getErrors(conversion), getSuccess(conversion), elapsedTime);
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
