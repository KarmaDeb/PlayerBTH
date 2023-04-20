package ml.karmaconfigs.playerbth.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import ml.karmaconfigs.api.common.data.file.FileUtilities;
import ml.karmaconfigs.api.common.karma.file.yaml.FileCopy;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.version.checker.VersionUpdater;
import ml.karmaconfigs.api.common.version.updater.VersionCheckType;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.commands.StaffCommands;
import ml.karmaconfigs.playerbth.commands.UserCommands;
import ml.karmaconfigs.playerbth.events.PlayerJoinEvent;
import ml.karmaconfigs.playerbth.metrics.Metrics;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.birthday.PlayerBTHExpansion;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.mysql.SQLPool;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PBTHPlugin implements PlayerBTH, Files {
  public final void initialize() {
    console.send("&b-------------------");
    console.send(" ");
    console.send("&aInitializing {0} &aversion {1}", name, version);
    console.send(" ");
    console.send("&b-------------------");
    initFiles();
    ((PluginCommand)Objects.<PluginCommand>requireNonNull(plugin.getCommand("bth"))).setExecutor((CommandExecutor)new UserCommands());
    ((PluginCommand)Objects.<PluginCommand>requireNonNull(plugin.getCommand("bths"))).setExecutor((CommandExecutor)new StaffCommands());
    plugin.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinEvent(), (Plugin)plugin);
    onInitialize();
    if (!plugin.getServer().getPluginManager().isPluginEnabled("NoteBlockAPI")) {
      console.send("NoteBlockAPI not found, we recommend you to download it if you want to play songs", Level.WARNING);
    } else {
      console.send("NoteBlockAPI found, hooking it", Level.OK);
    } 
    if (!plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      console.send("PlaceholderAPI not found, we recommend you to use it as it may provide util information", Level.WARNING);
    } else {
      console.send("PlaceholderAPI found, hooking it", Level.OK);
      PlayerBTHExpansion expansion = new PlayerBTHExpansion();
      expansion.register();
    } 
    startChecking();
    doMetrics();
  }
  
  public final void stop() {
    console.send("&b-------------------");
    console.send(" ");
    console.send("&cStopping {0}", name, version);
    console.send(" ");
    console.send("&b-------------------");
    SQLPool.terminateMySQL();
    onStop();
  }
  
  public void onInitialize() {}
  
  public void onStop() {}
  
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
      console.send("An error occurred while setting up files, check plugin logs for more info", Level.GRAVE);
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
    if (oldSongsFolder.exists() && 
      !newSongsFolder.exists() && 
      newSongsFolder.mkdirs() && 
      oldSongsFolder.renameTo(newSongsFolder))
      console.send("Changed old songs folder (Songs) to new one (songs)", Level.INFO); 
    File[] playersDat = (new File(plugin.getDataFolder() + "/Users")).listFiles();
    File oldPlayersDat = new File(plugin.getDataFolder() + "/Users");
    File newPlayersDat = new File(plugin.getDataFolder() + "/users");
    if (playersDat != null) {
      HashMap<File, Boolean> conversion = new HashMap<>();
      long start = System.nanoTime();
      for (File file : playersDat) {
        if (getExtension(file).equals("yml"))
          try {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            UUID uuid = UUID.fromString(Objects.<String>requireNonNull(yamlConfiguration.getString("UUID")));
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
            User user = new User(player);
            int day = Integer.parseInt(((String)Objects.requireNonNull(yamlConfiguration.getString("Birthday.Date"))).split("/")[0]);
            int month = Integer.parseInt(((String)Objects.requireNonNull(yamlConfiguration.getString("Birthday.Date"))).split("/")[1]);
            Birthday birthday = new Birthday(Month.byID(month), day);
            birthday.setAge(yamlConfiguration.getInt("Birthday.Age"));
            user.setBirthday(birthday);
            user.setNotifications(yamlConfiguration.getBoolean("Birthday.Public"));
            if (file.delete()) {
              console.send("Conversion of player file {0} to new player file complete", Level.OK,
                      FileUtilities.getPrettyFile(file));
            } else {
              console.send("Failed to delete old player file {0} and will be removed on exit", Level.WARNING,
                      FileUtilities.getPrettyFile(file));
              file.deleteOnExit();
            } 
            conversion.put(file, true);
          } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Failed to converse old player file {0} to new player file",
                    FileUtilities.getPrettyFile(file));

            conversion.put(file, false);
          }  
      } 
      if (!conversion.isEmpty()) {
        float elapsedTime = (float)(System.nanoTime() - start) / 1000.0F;
        if (oldPlayersDat.renameTo(newPlayersDat))
          console.send("Old player data conversion finished with a total of {0} errors, {1} success and took {2} ms", Level.INFO,
                  getErrors(conversion),
                  getSuccess(conversion),
                  elapsedTime);
      } 
    }

    if (config.getDataSystem().equals(DataSys.MYSQL)) {
      SQLPool pool = new SQLPool(config.mysqlHost(), config.mysqlDatabase(), config.mysqlTable(), config.mysqlUser(), config.mysqlPassword(), config.mysqlPort(), config.useSSL());
      pool.setOptions(config.getMaxConnections(), config.getMinConnections(), config.getConnectionTimeOut(), config.getConnectionLifeTime());
      pool.prepareTables();
    } 
  }
  
  private void startChecking() {
    VersionUpdater updater = VersionUpdater.createNewBuilder(plugin).withVersionType(VersionCheckType.NUMBER).build();
    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> doCheck(updater), 0, 20 * config.checkInterval());
    doCheck(updater);
  }

  private void doCheck(final VersionUpdater updater) {
    updater.fetch(true).whenComplete((result) -> {
      if (!result.isUpdated()) {
        console.send("There's a new version for PlayerBTH! Current version is {0}, and latest is {1}", result.getCurrent(), result.getLatest());
        for (String changelogLine : result.getChangelog()) console.send(changelogLine);
      }
    });
  }

  private void doMetrics() {
    Metrics metrics = new Metrics(plugin, 6068);
    metrics.getPluginData();
  }
  
  private int getErrors(HashMap<File, Boolean> map) {
    int errors = 0;
    for (Boolean bool : map.values()) {
      if (bool.equals(false))
        errors++; 
    } 
    return errors;
  }
  
  private int getSuccess(HashMap<File, Boolean> map) {
    int success = 0;
    for (Boolean bool : map.values()) {
      if (bool.equals(true))
        success++; 
    } 
    return success;
  }
  
  private String getExtension(File file) {
    String fileName = file.getName();
    String[] dots = fileName.split("\\.");
    return dots[dots.length - 1];
  }
}
