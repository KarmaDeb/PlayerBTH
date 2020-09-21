package ml.karmaconfigs.playerbth.Utils.Files;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public final class YamlManager implements PlayerBTH {
    
    private final File managed;
    private final YamlConfiguration file;

    /**
     * Starts the file manager
     *
     * @param fileName the file name
     */
    public YamlManager(String fileName) {
        this.managed = new File(plugin.getDataFolder(), fileName);
        if (!managed.exists()) {
            YamlCreator creator = new YamlCreator(fileName, false);
            creator.createFile();
        }
        this.file = YamlConfiguration.loadConfiguration(managed);
    }

    /**
     * Starts the file manager
     *
     * @param fileName the file name
     * @param fileDir  the file directory
     */
    public YamlManager(String fileName, String fileDir) {
        this.managed = new File(plugin.getDataFolder() + "/" + fileDir, fileName);
        if (!managed.exists()) {
            YamlCreator creator = new YamlCreator(fileName, fileDir, false);
            creator.createFile();
        }
        this.file = YamlConfiguration.loadConfiguration(managed);
    }

    /**
     * Gets the managed file
     *
     * @return file
     */
    public final File getManaged() {
        return managed;
    }

    /**
     * Gets the managed file configuration
     *
     * @return YamlConfiguration format file configuration
     */
    public final YamlConfiguration getFile() {
        return file;
    }

    /**
     * Set a path with no info
     *
     * @param path the path
     */
    public final void set(String path) {
        file.set(path, "");
        save();
    }

    /**
     * Set a path value as object
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Object value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as object
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, String value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as a string list
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, List<String> value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as boolean
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Boolean value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as integer
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Integer value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as double
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Double value) {
        file.set(path, value);
        save();
    }

    /**
     * Set a path value as float
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Float value) {
        file.set(path, value);
        save();
    }

    /**
     * Removes a path
     *
     * @param path the path
     */
    public final void unset(String path) {
        file.set(path, null);
        save();
    }

    /**
     * Check if the path is
     * empty
     *
     * @param path the path
     * @return a boolean
     */
    public final boolean isEmpty(String path) {
        if (isSet(path)) {
            return get(path).toString().isEmpty();
        } else {
            return true;
        }
    }

    /**
     * Check if the path is
     * set
     *
     * @param path the path
     * @return a boolean
     */
    public final boolean isSet(String path) {
        return get(path) != null;
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return object
     */
    public final Object get(String path) {
        return file.get(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return string
     */
    public final String getString(String path) {
        return file.getString(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return list of strings
     */
    public final List<String> getList(String path) {
        return file.getStringList(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return boolean
     */
    public final Boolean getBoolean(String path) {
        return file.getBoolean(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return integer
     */
    public final Integer getInt(String path) {
        return file.getInt(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return double
     */
    public final Double getDouble(String path) {
        return file.getDouble(path);
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return float
     */
    public final Float getFloat(String path) {
        return (float) file.getDouble(path);
    }

    /**
     * Removes the managing file
     */
    public final void delete() {
        String path = managed.getPath().replaceAll("\\\\", "/");

        if (managed.delete()) {
            Server.send("Deleted file " + path, Server.AlertLevel.INFO);
        } else {
            Server.send("Coudln't delete file {0}, will be deleted on exit", Server.AlertLevel.INFO, path);
            managed.deleteOnExit();
        }
    }

    /**
     * Save the file
     */
    public final void save() {
        String path = managed.getPath().replaceAll("\\\\", "/");

        try {
            file.save(managed);
        } catch (Throwable e) {
            Server.send("An internal error occurred while saving file " + path, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        }
    }
}
