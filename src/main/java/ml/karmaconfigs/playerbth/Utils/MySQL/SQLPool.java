package ml.karmaconfigs.playerbth.Utils.MySQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ml.karmaconfigs.playerbth.Utils.Server;

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
public final class SQLPool {

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
        config.setMaxLifetime(lifetime * 1000);
        config.setConnectionTimeout(timeout * 1000);
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
        } catch (Throwable e) {
            Server.send("An internal error occurred while creating MySQL tables", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
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
        } catch (Throwable e) {
            Server.send("An internal error occurred while updating MySQL tables", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            close(connection, statement);
        }

        if (changes) {
            Server.send("MySQL tables updated", Server.AlertLevel.INFO);
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
        } catch (Throwable e) {
            Server.send("An internal error occurred while checking MySQL column " + column, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return false;
        }
    }
}
