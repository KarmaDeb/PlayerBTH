package ml.karmaconfigs.playerbth.Utils.MySQL;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.Birthday.Month;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.OfflinePlayer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public final class Utils implements PlayerBTH {

    private final String table = SQLPool.getTable();
    private final String uuid;

    /**
     * Starts the MySQL management
     *
     * @param player the player
     */
    public Utils(OfflinePlayer player) {
        uuid = player.getUniqueId().toString();
    }

    /**
     * Checks if the MySQL user exists
     *
     * @return a boolean
     */
    public final boolean userExists() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            statement.setString(1, uuid);

            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (Throwable e) {
            Server.send("An internal error occurred while checking MySQL user", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return false;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Creates user on MySQL tables
     */
    public final void createUser() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);

            ResultSet results = statement.executeQuery();
            results.next();
            if (!userExists()) {
                PreparedStatement add = connection.prepareStatement("INSERT INTO " + table + "(UUID,BIRTHDAY,AGE,NOTIFY,CELEBRATE) VALUE (?,?,?,?,?)");

                add.setString(1, uuid);
                add.setString(2, "00-00");
                add.setInt(3, 1);
                add.setBoolean(4, true);
                add.setString(5, "");
                add.executeUpdate();
            }
        } catch (Throwable e) {
            Server.send("An internal error occurred while creating MySQL user", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Removes the player birthday
     */
    public final void removeUser() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("DELETE FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);

            statement.executeUpdate();
        } catch (Throwable e) {
            try {
                connection = SQLPool.getBucket().getConnection();
                statement = Objects.requireNonNull(connection).prepareStatement("DELETE * FROM " + table + " WHERE UUID=?");

                statement.setString(1, uuid);

                statement.executeUpdate();
            } catch (Throwable ex) {
                Server.send("An internal error occurred while removing MySQL user", Server.AlertLevel.ERROR);
                Server.send("&c" + ex.fillInStackTrace());
                for (StackTraceElement stack : ex.getStackTrace()) {
                    Server.send("&b                       " + stack);
                }
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Set the player birthday
     *
     * @param birthday the birthday
     */
    public final void setBirthday(Birthday birthday) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("UPDATE " + table + " SET BIRTHDAY=? WHERE UUID=?");

            statement.setString(2, uuid);
            statement.setString(1, birthday.getDay() + "-" + birthday.getMonth());

            statement.executeUpdate();
            setAge(birthday.getAge());
        } catch (Throwable e) {
            Server.send("An internal error occurred while setting MySQL user birthday", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Set the birthday age
     *
     * @param age the age
     */
    private void setAge(int age) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("UPDATE " + table + " SET AGE=? WHERE UUID=?");

            statement.setString(2, uuid);
            statement.setInt(1, age);

            statement.executeUpdate();
        } catch (Throwable e) {
            Server.send("An internal error occurred while setting MySQL user birthday", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Set if the player wants to receive other players
     * birthday notifications
     *
     * @param val the value
     */
    public final void setNotifications(boolean val) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("UPDATE " + table + " SET NOTIFY=? WHERE UUID=?");

            statement.setString(2, uuid);
            statement.setBoolean(1, val);

            statement.executeUpdate();
        } catch (Throwable e) {
            Server.send("An internal error occurred while setting MySQL user notifications", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    public final void setCelebrate(String dateFormat) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("UPDATE " + table + " SET CELEBRATE=? WHERE UUID=?");

            statement.setString(2, uuid);
            statement.setString(1, dateFormat);

            statement.executeUpdate();
        } catch (Throwable e) {
            Server.send("An internal error occurred while setting MySQL user celebration date", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Check if the user has birthday
     *
     * @return a boolean
     */
    public final boolean hasBirthday() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            return !results.getString("BIRTHDAY").isEmpty() && !results.getString("BIRTHDAY").equals("00-00");
        } catch (Throwable e) {
            Server.send("An internal error occurred while checking MySQL user birthday", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return false;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Check if the user has birthday
     *
     * @return a boolean
     */
    public final Birthday getBirthday() {
        Birthday birthday = new Birthday(Month.byID(getBirthdayMonth()), getBirthdayDay());
        birthday.setAge(getAge());

        return birthday;
    }

    /**
     * Get the player birthday month
     *
     * @return an integer
     */
    private int getBirthdayMonth() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            return Integer.parseInt(results.getString("BIRTHDAY").split("-")[1]);
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL user birthday month", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return 1;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Get the player birthday day
     *
     * @return an integer
     */
    private int getBirthdayDay() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            return Integer.parseInt(results.getString("BIRTHDAY").split("-")[0]);
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL user birthday day", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return 1;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Get the player age
     *
     * @return an integer
     */
    private int getAge() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            return Integer.parseInt(results.getString("AGE"));
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL user age", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return 1;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Check if the player wants to receive
     * other players birthdays notifications
     *
     * @return a boolean
     */
    public final boolean hasNotifications() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            return Integer.parseInt(results.getString("NOTIFY")) == 1;
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL user notifications", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
            return true;
        } finally {
            SQLPool.close(connection, statement);
        }
    }

    /**
     * Check if the player last celebration date
     * compared with today's date is a new day
     *
     * @return  a boolean
     */
    public final boolean isCelebrated() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");

            statement.setString(1, uuid);
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
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL user celebrate date", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }
        return false;
    }

    /**
     * Get a list of all the players
     * with birthdays registered in the
     * MySQL database
     *
     * @return an arraylist of offline
     * players
     */
    public static ArrayList<OfflinePlayer> getPlayers() {
        Connection connection = null;
        PreparedStatement statement = null;
        ArrayList<OfflinePlayer> players = new ArrayList<>();

        try {
            connection = SQLPool.getBucket().getConnection();
            statement = Objects.requireNonNull(connection).prepareStatement("SELECT * FROM " + SQLPool.getTable());

            ResultSet results = statement.executeQuery();
            while (results.next()) {
                players.add(plugin.getServer().getOfflinePlayer(UUID.fromString(results.getString("UUID"))));
            }
        } catch (Throwable e) {
            Server.send("An internal error occurred while getting MySQL users", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            SQLPool.close(connection, statement);
        }

        return players;
    }
}
