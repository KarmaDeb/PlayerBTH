package ml.karmaconfigs.playerbth.utils.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public final class SQLPool implements PlayerBTH {

    private static int max = 3, min = 10, timeout = 40, lifetime = 300;
    private static String host, database, table, username, password;
    private static int port;

    private static HikariDataSource dataSource;

    /**
     * Initialize the Bucket <code>
     * Connection pool
     * </code> connection
     */
    public SQLPool(String host, String database, String table, String user, String password, int port, boolean useSSL) {
        SQLPool.host = host;
        SQLPool.database = database + "?autoReconnect=true&useSSL=" + useSSL;
        if (!table.contains("_")) {
            SQLPool.table = "bth_" + table;
        } else {
            SQLPool.table = table;
        }
        SQLPool.username = user;
        SQLPool.password = password;
        SQLPool.port = port;
    }

    /**
     * Close the connection and return it to
     * the connection pool
     */
    public static void close(Connection connection, PreparedStatement statement) {
        if (connection != null) try {
            connection.close();
        } catch (Throwable ignored) {}

        if (statement != null) try {
            statement.close();
        } catch (Throwable ignored) {}
    }

    /**
     * Terminate the MySQL connection pool
     */
    public static void terminateMySQL() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Get the MySQL connection
     *
     * @return a connection
     */
    public static HikariDataSource getBucket() {
        return dataSource;
    }

    /**
     * Get the MySQL table
     *
     * @return a String
     */
    public static String getTable() {
        return table;
    }

    /**
     * Get the MySQL password
     *
     * @return a String
     */
    public static String getPassword() {
        return password;
    }

    /**
     * Get the MySQL port
     *
     * @return an integer
     */
    public static int getPort() {
        return port;
    }

    /**
     * Set extra Bucket options
     *
     * @param max the max amount of connections
     * @param min the minimum amount of connections
     * @param timeout the connections time outs
     * @param lifetime the connection life time
     */
    public final void setOptions(int max, int min, int timeout, int lifetime) {
        SQLPool.max = max;
        SQLPool.min = min;
        SQLPool.timeout = timeout;
        SQLPool.lifetime = lifetime;

        setup();
    }

    /**
     * Setup the bucket connection
     * <code>Initialize the pool of connections</code>
     */
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

    /**
     * Initialize the MySQL tables
     */
    public final void prepareTables() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " " + "(UUID text, BIRTHDAY text, AGE integer, " +
                    "NOTIFY boolean, CELEBRATE text)");

            statement.executeUpdate();
            updateTables();
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Error while preparing sql tables");
            Console.send(plugin, "An error occurred while preparing tables for SQL", Level.GRAVE);
        } finally {
            close(connection, statement);
        }
    }

    /**
     * Update tables to 3.0.2 version
     * witch had a lot of changes in
     * the whole code
     */
    public final void updateTables() {
        boolean changes = false;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            if (!columnExists("NOTIFY")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "ADD NOTIFY boolean");
                statement.executeUpdate();
                changes = true;
            }
            if (columnExists("NAME")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "DROP NAME");
                statement.executeUpdate();
                changes = true;
            }
            if (columnExists("BTHSET")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "DROP BTHSET");
                statement.executeUpdate();
                changes = true;
            }
            if (columnExists("SETON")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "DROP SETON");
                statement.executeUpdate();
                changes = true;
            }
            if (columnExists("PUBLIC")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "DROP PUBLIC");
                statement.executeUpdate();
                changes = true;
            }
            if (columnExists("CELEBRATED")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "DROP CELEBRATED");
                statement.executeUpdate();
                changes = true;
            }
            if (!columnExists("CELEBRATE")) {
                statement = connection.prepareStatement("ALTER TABLE " + table + " " + "ADD CELEBRATE text");
                statement.executeUpdate();
                changes = true;
            }
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Error while updating sql tables");
            Console.send(plugin, "An error occurred while updating tables for SQL", Level.GRAVE);
        } finally {
            close(connection, statement);
        }

        if (changes) {
            Console.send(plugin, "MySQL tables updated", Level.INFO);
        }
    }

    /**
     * Check if the specified column exists
     *
     * @param column the column
     * @return a boolean
     */
    private boolean columnExists(String column) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DatabaseMetaData md = dataSource.getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, table, column);
            return rs.next();
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Error while checking for sql column existence");
            Console.send(plugin, "An error occurred while checking for sql column existence", Level.GRAVE);
            return false;
        }
    }
}
