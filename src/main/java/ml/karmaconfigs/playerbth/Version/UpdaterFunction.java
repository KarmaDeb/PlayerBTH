package ml.karmaconfigs.playerbth.Version;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Files.Files;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public class UpdaterFunction implements PlayerBTH {

    private final int latest;
    private final int actual;

    /**
     * Starts the version checker for PlayerBTH
     */
    public UpdaterFunction() {
        String current = plugin.getDescription().getVersion().replaceAll("[A-z]", "").replace(".", "");
        this.actual = Integer.parseInt(current.replace(" ", ""));
        this.latest = new GetLatestVersion().GetLatest();
    }

    /**
     * Check if the plugin is outdated
     *
     * @return if the plugin is outdated
     */
    public boolean isOutdated() {
        if (actual != latest) {
            return actual <= latest;
        } else {
            return false;
        }
    }

    /**
     * Execute the version check task
     */
    public void checkVersion() {
        if (isOutdated()) {
            Server.send("&7Birthday &f>> &cNew version available for PlayerBTH ( " + new GetLatestVersion().getVersionString() + " )");
            if (!Files.config.downloadToUpdate()) {
                Server.send("&bDownload the latest version from &3https://www.spigotmc.org/resources/playerbirthday.73424/");
            } else {
                try {
                    DownloadLatest latest = new DownloadLatest();

                    latest.download();
                    Server.send("&aDownloaded latest version of PlayerBTH ( " + new GetLatestVersion().getVersionString() + " ) and will be installed on next server start");
                } catch (Throwable e) {
                    Server.send("&bTried to download latest PlayerBTH but got an error, download the latest version from &3https://www.spigotmc.org/resources/playerbirthday.73424/");
                }
            }
            if (Files.config.sendChangeLogs()) {
                new GetLatestVersion().sendChangeLogConsole();
            }
        } else {
            if (Files.config.notifyUpdated()) {
                Server.send("&7Birthday &f>> &aYou are using the latest version of PlayerBTH &7( &f" + new GetLatestVersion().getVersionString() + " &7)");
            }
        }
    }
}
