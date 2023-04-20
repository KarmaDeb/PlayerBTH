package ml.karmaconfigs.playerbth.utils.files;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ml.karmaconfigs.api.common.karma.file.yaml.KarmaYamlManager;
import ml.karmaconfigs.api.common.karma.file.yaml.YamlReloader;
import ml.karmaconfigs.playerbth.utils.DataSys;

import static ml.karmaconfigs.playerbth.PlayerBTH.*;

public final class Config {
  private static final File file = new File(plugin.getDataFolder(), "config.yml");
  
  private static final KarmaYamlManager config = new KarmaYamlManager(file);
  
  public interface manager {
    static boolean reload() {
      YamlReloader reloader = Config.config.getReloader();
      if (reloader != null) {
        reloader.reload();
        return true;
      } 
      return false;
    }
  }
  
  private boolean getBoolean(String path) {
    return config.getBoolean(path, false);
  }
  
  private int getInt(String path) {
    return config.getInt(path, 0);
  }
  
  private String getString(String path) {
    return config.getString(path, "");
  }
  
  public boolean enableSong() {
    return getBoolean("Song.enabled");
  }
  
  public String getSong() {
    return getString("Song.Name");
  }
  
  public boolean enableFireWorks() {
    return getBoolean("Firework.enabled");
  }
  
  public int fireworkPower() {
    return getInt("Firework.power");
  }
  
  public int fireworkAmount() {
    return getInt("Firework.amount");
  }

  public boolean usFormat() {
    return getBoolean("UsFormat");
  }

  public boolean giveCake() {
    return getBoolean("Cake");
  }
  
  public DataSys getDataSystem() {
    try {
      return DataSys.valueOf(getString("DataSys").toUpperCase());
    } catch (Throwable e) {
      return DataSys.FILE;
    } 
  }
  
  public long checkInterval() {
    return TimeUnit.MINUTES.toSeconds(getInt("UpdateInterval"));
  }
  
  public String birthdayNotSet() {
    return getString("BdNotSet");
  }
  
  public String birthdaySet() {
    return getString("BdSet");
  }
  
  public String mysqlHost() {
    return getString("MySQL.Host");
  }
  
  public int mysqlPort() {
    return getInt("MySQL.Port");
  }
  
  public String mysqlUser() {
    return getString("MySQL.User");
  }
  
  public String mysqlPassword() {
    return getString("MySQL.Password");
  }
  
  public String mysqlDatabase() {
    return getString("MySQL.Database");
  }
  
  public String mysqlTable() {
    return getString("MySQL.Table");
  }
  
  public boolean useSSL() {
    return getBoolean("MySQL.SSL");
  }
  
  public int getMinConnections() {
    return getInt("MySQL.Connection.Min");
  }
  
  public int getMaxConnections() {
    return getInt("MySQL.Connection.Max");
  }
  
  public int getConnectionTimeOut() {
    return getInt("MySQL.Connection.TimeOut");
  }
  
  public int getConnectionLifeTime() {
    return getInt("MySQL.Connection.LifeTime");
  }
}
