package ml.karmaconfigs.playerbth.version;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
public class GetLatestVersion implements PlayerBTH {

    public static final String VERSION = "Latest version getter";

    public int latest;

    private String version = "";

    private final List<String> replaced = new ArrayList<>();


    /**
     * Starts retrieving the info from the html file
     */

    public GetLatestVersion() {
        try {
            URL url = new URL("https://karmaconfigs.github.io/updates/PlayerBTH/latest.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String word;
            List<String> lines = new ArrayList<>();
            while ((word = reader.readLine()) != null)
                if (!lines.contains(word)) {
                    lines.add(word);
                }
            reader.close();
            for (String str : lines) {
                if (!replaced.contains(str)) {
                    replaced.add(str
                            .replace("[", "{replace_open}")
                            .replace("]", "{replace_close}")
                            .replace(",", "{replace_comma}"));
                }
            }
            this.latest = Integer.parseInt(replaced.get(0).replaceAll("[aA-zZ]", "").replace(".", ""));
            this.version = replaced.get(0);
        } catch (Throwable ex) {
            logger.scheduleLog(Level.GRAVE, ex);
            logger.scheduleLog(Level.INFO, "Failed to perform a connection with the update system");
            Console.send(plugin, "Couldn't make a connection with update system", Level.GRAVE);
        }
    }

    /**
     *
     * @return the latest version as integer
     */
    public int GetLatest() {
        return latest;
    }

    /**
     *
     * @return the latest version status (Beta - Alpha - Release) and his version int
     */
    public String getVersionString() {
        String v = version.replaceAll("[A-z]", "");
        String versionTxt = version.replaceAll("[0-9]", "").replace(".", "").replace(" ", "");
        if (!versionTxt.isEmpty()) {
            return versionTxt + " / " + v.replace(" ", "");
        } else {
            return v.replace(" ", "");
        }
    }

    /**
     * Sends the list of changes to the console
     */
    public void sendChangeLogConsole() {
        for (int i = 0; i < replaced.size(); i++) {
            if (i == 0) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b--------- &eChangeLog &6: &a{version} &b---------")
                        .replace("{version}", replaced.get(0)));
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + replaced.get(i)
                        .replace("replace_one", "[")
                        .replace("replace_two", "]")
                        .replace("replace_comma", ",")));
            }
        }
    }
}
