package ml.karmaconfigs.playerbth.Version;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Server;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public final class DownloadLatest implements PlayerBTH {

    private final File update = new File(plugin.getServer().getWorldContainer() + "/plugins/update");
    private final File destJar = new File(plugin.getServer().getWorldContainer() + "/plugins/update", PlayerBTH.getJarName());

    private final URL downloadURL;

    /**
     * Initialize the downloader
     *
     * @throws Throwable any kind of exception or error
     */
    public DownloadLatest() throws Throwable {
        downloadURL = new URL("https://karmaconfigs.github.io/updates/PlayerBTH/PlayerBTH_" + new GetLatestVersion().GetLatest() + ".jar");
    }

    /**
     *  Download the latest PlayerBTH jar version
     */
    public final void download() {
        try {
            int count;
            URLConnection connection = downloadURL.openConnection();
            connection.connect();

            if (!update.exists()) {
                if (update.mkdir()) {
                    Server.send("Created update folder for PlayerBTH new update", Server.AlertLevel.INFO);
                } else {
                    Server.send("An unknown error occurred while creating update folder", Server.AlertLevel.ERROR);
                }
            }

            InputStream input = new BufferedInputStream(downloadURL.openStream(), 1024);
            OutputStream output = new FileOutputStream(destJar);

            byte[] data = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();

            output.close();
            input.close();
        } catch (Throwable e) {
            Server.send("An internal error occurred while downloading latest PlayerBTH version", Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        }
    }
}
