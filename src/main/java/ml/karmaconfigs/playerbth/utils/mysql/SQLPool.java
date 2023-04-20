package ml.karmaconfigs.playerbth.utils.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;

public final class SQLPool implements PlayerBTH {
  private static int max = 3;
  
  private static int min = 10;
  
  private static int timeout = 40;
  
  private static int lifetime = 300;
  
  private static String host;
  
  private static String database;
  
  private static String table;
  
  private static String username;
  
  private static String password;
  
  private static int port;
  
  private static HikariDataSource dataSource;
  
  public SQLPool(String host, String database, String table, String user, String password, int port, boolean useSSL) {
    SQLPool.host = host;
    SQLPool.database = database + "?autoReconnect=true&useSSL=" + useSSL;
    if (!table.contains("_")) {
      SQLPool.table = "bth_" + table;
    } else {
      SQLPool.table = table;
    } 
    username = user;
    SQLPool.password = password;
    SQLPool.port = port;
  }
  
  public static void close(Connection connection, PreparedStatement statement) {
    if (connection != null)
      try {
        connection.close();
      } catch (Throwable throwable) {} 
    if (statement != null)
      try {
        statement.close();
      } catch (Throwable throwable) {} 
  }
  
  public static void terminateMySQL() {
    if (dataSource != null)
      dataSource.close(); 
  }
  
  public static HikariDataSource getBucket() {
    return dataSource;
  }
  
  public static String getTable() {
    return table;
  }
  
  public static String getPassword() {
    return password;
  }
  
  public static int getPort() {
    return port;
  }
  
  public final void setOptions(int max, int min, int timeout, int lifetime) {
    SQLPool.max = max;
    SQLPool.min = min;
    SQLPool.timeout = timeout;
    SQLPool.lifetime = lifetime;
    setup();
  }
  
  private void setup() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
    config.setDriverClassName("com.mysql.jdbc.Driver");
    config.setUsername(username);
    config.setPassword(password);
    config.setMinimumIdle(min);
    config.setMaximumPoolSize(max);
    config.setMaxLifetime(lifetime * 1000L);
    config.setConnectionTimeout(timeout * 1000L);
    config.setConnectionTestQuery("SELECT 1");
    dataSource = new HikariDataSource(config);
  }
  
  public final void prepareTables() {
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = dataSource.getConnection();
      statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (UUID text, BIRTHDAY text, AGE integer, NOTIFY boolean, PRIVATE boolean, CELEBRATE text)");
      statement.executeUpdate();
      updateTables();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Error while preparing sql tables", new Object[0]);
      console.send("An error occurred while preparing tables for SQL", Level.GRAVE);
    } finally {
      close(connection, statement);
    } 
  }
  
  public final void updateTables() {
    boolean changes = false;
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = dataSource.getConnection();
      if (!columnExists("NOTIFY")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " ADD NOTIFY boolean");
        statement.executeUpdate();
        changes = true;
      }
      if (!columnExists("PRIVATE")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " ADD PRIVATE boolean");
        statement.executeUpdate();
        changes = true;
      }
      if (columnExists("NAME")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " DROP NAME");
        statement.executeUpdate();
        changes = true;
      } 
      if (columnExists("BTHSET")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " DROP BTHSET");
        statement.executeUpdate();
        changes = true;
      } 
      if (columnExists("SETON")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " DROP SETON");
        statement.executeUpdate();
        changes = true;
      } 
      if (columnExists("PUBLIC")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " DROP PUBLIC");
        statement.executeUpdate();
        changes = true;
      } 
      if (columnExists("CELEBRATED")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " DROP CELEBRATED");
        statement.executeUpdate();
        changes = true;
      } 
      if (!columnExists("CELEBRATE")) {
        statement = connection.prepareStatement("ALTER TABLE " + table + " ADD CELEBRATE text");
        statement.executeUpdate();
        changes = true;
      } 
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Error while updating sql tables", new Object[0]);
      console.send("An error occurred while updating tables for SQL", Level.GRAVE);
    } finally {
      close(connection, statement);
    } 
    if (changes)
      console.send("MySQL tables updated", Level.INFO); 
  }
  
  private boolean columnExists(String column) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      DatabaseMetaData md = dataSource.getConnection().getMetaData();
      ResultSet rs = md.getColumns(null, null, table, column);
      return rs.next();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Error while checking for sql column existence", new Object[0]);
      console.send("An error occurred while checking for sql column existence", Level.GRAVE);
      return false;
    } 
  }
}
