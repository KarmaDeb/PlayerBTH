package ml.karmaconfigs.playerbth.utils.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import org.bukkit.OfflinePlayer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Utils implements PlayerBTH {
  private final String table = SQLPool.getTable();
  
  private final String uuid;
  
  public Utils(OfflinePlayer player) {
    this.uuid = player.getUniqueId().toString();
  }
  
  public final boolean notExists() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      return !results.next();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to check for user existence of {0}", new Object[] { this.uuid });
      console.send("Failed to check existence of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return true;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final void createUser() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      if (notExists()) {
        PreparedStatement add = connection.prepareStatement("INSERT INTO " + this.table + "(UUID,BIRTHDAY,AGE,NOTIFY,CELEBRATE) VALUE (?,?,?,?,?)");
        add.setString(1, this.uuid);
        add.setString(2, "00-00");
        add.setInt(3, 1);
        add.setBoolean(4, true);
        add.setString(5, "");
        add.executeUpdate();
      } 
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to create tables for user {0}", new Object[] { this.uuid });
      console.send("An error occurred while creating tables for user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final void removeUser() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("DELETE FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      statement.executeUpdate();
    } catch (Throwable e) {
      try {
        connection = SQLPool.getBucket().getConnection();
        statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("DELETE * FROM " + this.table + " WHERE UUID=?");
        statement.setString(1, this.uuid);
        statement.executeUpdate();
      } catch (Throwable ex) {
        logger.scheduleLog(Level.GRAVE, ex);
        logger.scheduleLog(Level.INFO, "Failed to remove tables of user {0}", new Object[] { this.uuid });
        console.send("An error occurred while removing tables from user {0}", Level.GRAVE, new Object[] { this.uuid });
      } 
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final void setBirthday(Birthday birthday) {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("UPDATE " + this.table + " SET BIRTHDAY=? WHERE UUID=?");
      statement.setString(2, this.uuid);
      statement.setString(1, birthday.getDay() + "-" + birthday.getMonth());
      statement.executeUpdate();
      setAge(birthday.getAge());
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to set birthday of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while setting birthday of user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  private void setAge(int age) {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("UPDATE " + this.table + " SET AGE=? WHERE UUID=?");
      statement.setString(2, this.uuid);
      statement.setInt(1, age);
      statement.executeUpdate();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to set age of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while setting age of user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final void setNotifications(boolean val) {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("UPDATE " + this.table + " SET NOTIFY=? WHERE UUID=?");
      statement.setString(2, this.uuid);
      statement.setBoolean(1, val);
      statement.executeUpdate();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to set notification status of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while setting notifications for user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
  }

  public void setPrivate(final boolean val) {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = (Objects.<Connection>requireNonNull(connection)).prepareStatement("UPDATE " + this.table + " SET PRIVATE=? WHERE UUID=?");
      statement.setString(2, this.uuid);
      statement.setBoolean(1, val);
      statement.executeUpdate();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to set private status of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while setting private for user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    }
  }
  
  public final void setCelebrate(String dateFormat) {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("UPDATE " + this.table + " SET CELEBRATE=? WHERE UUID=?");
      statement.setString(2, this.uuid);
      statement.setString(1, dateFormat);
      statement.executeUpdate();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to set birthday celebration of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while setting birthday celebration of user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final boolean hasBirthday() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return (!results.getString("BIRTHDAY").isEmpty() && !results.getString("BIRTHDAY").equals("00-00"));
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while getting birthday of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while getting birthday of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return false;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final Birthday getBirthday() {
    Birthday birthday = new Birthday(Month.byID(getBirthdayMonth()), getBirthdayDay());
    birthday.setAge(getAge());
    return birthday;
  }
  
  private int getBirthdayMonth() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return Integer.parseInt(results.getString("BIRTHDAY").split("-")[1]);
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while set getting birthday of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while getting birthday of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return 1;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  private int getBirthdayDay() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return Integer.parseInt(results.getString("BIRTHDAY").split("-")[0]);
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while getting birthday day of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while getting birthday day of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return 1;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  private int getAge() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return Integer.parseInt(results.getString("AGE"));
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while getting age of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while getting age of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return 1;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }
  
  public final boolean hasNotifications() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return (Integer.parseInt(results.getString("NOTIFY")) == 1);
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while notifications of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while checking notifications of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return true;
    } finally {
      SQLPool.close(connection, statement);
    } 
  }

  public boolean isPrivate() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      return (Integer.parseInt(results.getString("PRIVATE")) == 1);
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while private of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while checking private of user {0}", Level.GRAVE, new Object[] { this.uuid });
      return true;
    } finally {
      SQLPool.close(connection, statement);
    }
  }
  
  public final boolean isCelebrated() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
      statement.setString(1, this.uuid);
      ResultSet results = statement.executeQuery();
      results.next();
      String data = results.getString("CELEBRATE");
      if (!data.isEmpty()) {
        DateTimeFormatter now = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime time = now.parseDateTime(data);
        if (time.plusDays(1).isBeforeNow()) {
          if (time.getDayOfMonth() + 1 == DateTime.now().getDayOfMonth()) {
            String format = DateTime.now().year().get() + "/" + DateTime.now().monthOfYear().get() + "/" + DateTime.now().dayOfMonth().get() + " " + time.getHourOfDay() + ":" + time.getMinuteOfHour() + ":" + time.getSecondOfMinute();
            time = now.parseDateTime(format);
            return time.plusHours(24).isBeforeNow();
          } 
          return true;
        } 
        return false;
      } 
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while getting birthday celebration of user {0}", new Object[] { this.uuid });
      console.send("An error occurred while getting birthday celebration of user {0}", Level.GRAVE, new Object[] { this.uuid });
    } finally {
      SQLPool.close(connection, statement);
    } 
    return false;
  }
  
  public static ArrayList<OfflinePlayer> getPlayers() {
    Connection connection = null;
    PreparedStatement statement = null;
    ArrayList<OfflinePlayer> players = new ArrayList<>();
    try {
      connection = SQLPool.getBucket().getConnection();
      statement = ((Connection)Objects.<Connection>requireNonNull(connection)).prepareStatement("SELECT * FROM " + SQLPool.getTable());
      ResultSet results = statement.executeQuery();
      while (results.next())
        players.add(plugin.getServer().getOfflinePlayer(UUID.fromString(results.getString("UUID")))); 
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed while getting birthday players", new Object[0]);
      console.send("An error occurred while getting all birthday players", Level.GRAVE);
    } finally {
      SQLPool.close(connection, statement);
    } 
    return players;
  }
}
