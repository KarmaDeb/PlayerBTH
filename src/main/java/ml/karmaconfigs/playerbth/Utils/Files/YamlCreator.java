package ml.karmaconfigs.playerbth.Utils.Files;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

@Deprecated
public final class YamlCreator implements PlayerBTH {

    private final File folder;
    private final File file;

    private final boolean isResource;

    private YamlConfiguration config;
    private YamlConfiguration cfg;

    /**
     * Starts the file creator
     *
     * @param fileName   the file name
     * @param fileDir    the file dir
     * @param isResource if the file is inside the plugin itself
     */
    public YamlCreator(String fileName, String fileDir, boolean isResource) {
        this.isResource = isResource;
        if (isResource) {
            InputStream theFile = (plugin).getClass().getResourceAsStream("/" + fileName);
            InputStreamReader DF = new InputStreamReader(theFile, StandardCharsets.UTF_8);
            cfg = YamlConfiguration.loadConfiguration(DF);
        }

        file = new File(plugin.getDataFolder() + File.separator + fileDir, fileName);
        folder = new File(plugin.getDataFolder() + File.separator + fileDir);
    }

    /**
     * Starts the file creator
     *
     * @param fileName   the file name
     * @param isResource if the file is inside the plugin itself
     */
    public YamlCreator(String fileName, boolean isResource) {
        this.isResource = isResource;
        if (isResource) {
            InputStream theFile = (plugin).getClass().getResourceAsStream("/" + fileName);
            InputStreamReader DF = new InputStreamReader(theFile, StandardCharsets.UTF_8);
            cfg = YamlConfiguration.loadConfiguration(DF);
        }

        file = new File(plugin.getDataFolder(), fileName);
        folder = plugin.getDataFolder();
    }

    /**
     * Starts the file creator
     *
     * @param fileName     the file name
     * @param resourceFile if the resource file is custom
     */
    public YamlCreator(String fileName, String resourceFile) {
        InputStream theFile = (plugin).getClass().getResourceAsStream("/" + resourceFile);
        if (theFile != null) {
            InputStreamReader DF = new InputStreamReader(theFile, StandardCharsets.UTF_8);
            cfg = YamlConfiguration.loadConfiguration(DF);
            isResource = true;
        } else {
            isResource = false;
        }
        file = new File(plugin.getDataFolder(), fileName);
        folder = plugin.getDataFolder();
    }

    /**
     * Starts the file creator
     *
     * @param fileName     the file name
     * @param fileDir      the file dir
     * @param resourceFile if the resource file is custom
     */
    public YamlCreator(String fileName, String fileDir, String resourceFile) {
        InputStream theFile = (plugin).getClass().getResourceAsStream("/" + resourceFile);
        if (theFile != null) {
            InputStreamReader DF = new InputStreamReader(theFile, StandardCharsets.UTF_8);
            cfg = YamlConfiguration.loadConfiguration(DF);
            isResource = true;
        } else {
            isResource = false;
        }

        this.file = new File(plugin.getDataFolder() + "/" + fileDir, fileName);
        this.folder = new File(plugin.getDataFolder() + "/" + fileDir);
    }

    /**
     * Create the file and the folder
     * if not exists
     */
    public final void createFile() {
        if (!folder.exists()) {
            String path = folder.getPath().replaceAll("\\\\", "/");
            if (folder.mkdirs()) {
                Server.send("The directory {0} have been created", Server.AlertLevel.INFO, path);
            } else {
                Server.send("An unknown error occurred while creating directory " + path, Server.AlertLevel.INFO);
            }
        }
        if (!file.exists()) {
            String path = file.getPath().replaceAll("\\\\", "/");
            try {
                if (file.createNewFile()) {
                    Server.send("The file {0} have been created", Server.AlertLevel.INFO, path);
                } else {
                    Server.send("An unknown error occurred while creating file " + path, Server.AlertLevel.INFO);
                }
            } catch (Throwable e) {
                Server.send("An internal error occurred while creating file " + path, Server.AlertLevel.ERROR);
                Server.send("&c" + e.fillInStackTrace());
                for (StackTraceElement stack : e.getStackTrace()) {
                    Server.send("&b                       " + stack);
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Set the defaults for the file
     * reading the internal file
     */
    public final void setDefaults() {
        createFile();
        if (isResource) {
            List<String> sections = new ArrayList<>();

            for (String path : config.getKeys(false)) {
                if (cfg.get(path) == null) {
                    config.set(path, null);
                }
            }

            for (String path : cfg.getKeys(false)) {
                if (config.get(path) == null) {
                    config.set(path, cfg.get(path));
                } else {
                    if (!(cfg.get(path) instanceof ConfigurationSection)) {
                        if (cfg.get(path) instanceof Boolean) {
                            if (!(config.get(path) instanceof Boolean)) {
                                config.set(path, cfg.getBoolean(path));
                            }
                        }
                        if (cfg.get(path) instanceof Integer) {
                            if (!(config.get(path) instanceof Integer)) {
                                config.set(path, cfg.getInt(path));
                            }
                        }
                        if (cfg.get(path) instanceof Double) {
                            if (!(config.get(path) instanceof Double)) {
                                config.set(path, cfg.getDouble(path));
                            }
                        }
                        if (cfg.get(path) instanceof Long) {
                            if (!(config.get(path) instanceof Long)) {
                                config.set(path, cfg.getLong(path));
                            }
                        }
                        if (cfg.get(path) instanceof String) {
                            if (!(config.get(path) instanceof String)) {
                                config.set(path, cfg.getString(path));
                            }
                        }
                        if (cfg.get(path) instanceof List) {
                            if (!(config.get(path) instanceof List)) {
                                config.set(path, cfg.getList(path));
                            }
                        }
                    } else {
                        sections.add(path);
                    }
                }
            }

            if (!sections.isEmpty()) {
                for (String sectionPath : sections) {
                    ConfigurationSection section = cfg.getConfigurationSection(sectionPath);
                    if (section != null) {
                        if (!section.getKeys(false).isEmpty()) {
                            generateSections(sectionPath);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate the sections for
     * the file
     *
     * @param section the configuration
     *                section
     */
    private void generateSections(String section) {

        ConfigurationSection sect = cfg.getConfigurationSection(section);

        List<String> sections = new ArrayList<>();

        if (config.isSet(section)) {
            for (String str : Objects.requireNonNull(config.getConfigurationSection(section)).getKeys(false)) {
                String path = section + "." + str;

                if (cfg.get(path) == null) {
                    config.set(path, null);
                }
            }
        }

        assert sect != null;
        for (String str : sect.getKeys(false)) {
            String path = section + "." + str;

            if (config.get(path) == null) {
                config.set(path, cfg.get(path));
            } else {
                if (!(cfg.get(path) instanceof ConfigurationSection)) {
                    if (cfg.get(path) instanceof Boolean) {
                        if (!(config.get(path) instanceof Boolean)) {
                            config.set(path, cfg.getBoolean(path));
                        }
                    }
                    if (cfg.get(path) instanceof Integer) {
                        if (!(config.get(path) instanceof Integer)) {
                            config.set(path, cfg.getInt(path));
                        }
                    }
                    if (cfg.get(path) instanceof Double) {
                        if (!(config.get(path) instanceof Double)) {
                            config.set(path, cfg.getDouble(path));
                        }
                    }
                    if (cfg.get(path) instanceof Long) {
                        if (!(config.get(path) instanceof Long)) {
                            config.set(path, cfg.getLong(path));
                        }
                    }
                    if (cfg.get(path) instanceof String) {
                        if (!(config.get(path) instanceof String)) {
                            config.set(path, cfg.getString(path));
                        }
                    }
                    if (cfg.get(path) instanceof List) {
                        if (!(config.get(path) instanceof List)) {
                            config.set(path, cfg.getList(path));
                        }
                    }
                } else {
                    sections.add(path);
                }
            }
        }

        if (!sections.isEmpty()) {
            for (String sectionPath : sections) {
                ConfigurationSection cfgSection = cfg.getConfigurationSection(sectionPath);
                if (cfgSection != null) {
                    if (!cfgSection.getKeys(false).isEmpty()) {
                        generateSections(sectionPath);
                    }
                }
            }
        }
    }

    /**
     * Save the file
     */
    public final void saveFile() {
        try {
            config.save(file);
        } catch (Throwable e) {
            String path = file.getPath().replaceAll("\\\\", "/");

            Server.send("An internal error occurred while saving file " + path, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        }
    }

    /**
     * Check if the file exists
     *
     * @return a boolean
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * Get the file
     *
     * @return a file
     */
    public final File getFile() {
        return file;
    }
}
