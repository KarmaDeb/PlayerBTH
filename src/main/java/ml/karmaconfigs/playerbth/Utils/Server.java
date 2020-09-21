package ml.karmaconfigs.playerbth.Utils;

import ml.karmaconfigs.playerbth.PlayerBTH;

import java.io.File;
import java.io.FileWriter;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public interface Server extends PlayerBTH {

    enum AlertLevel {
        WARNING,ERROR,INFO
    }

    static void send(String message) {
        plugin.getServer().getConsoleSender().sendMessage(StringUtils.toColor(message));
    }

    static void send(String message, Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            message = message.replace("{" + i + "}", replaces[i].toString());
        }

        send(message);
    }

    static void send(String message, AlertLevel level) {
        message = StringUtils.stripColor(message);
        switch (level) {
            case WARNING:
                send("&f[ &bPlayerBTH &f] &6WARNING&7: &e" + message);
                break;
            case ERROR:
                send("&f[ &bPlayerBTH &f] &4ERROR&7: &c" + message);
                break;
            default:
                send("&f[ &bPlayerBTH &f] &7INFO&f: &b" + message);
                break;
        }
    }

    static void send(String message, AlertLevel level, Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            message = message.replace("{" + i + "}", replaces[i].toString());
        }

        message = StringUtils.stripColor(message);
        switch (level) {
            case WARNING:
                send("&f[ &bPlayerBTH &f] &6WARNING&7: &e" + message);
                break;
            case ERROR:
                send("&f[ &bPlayerBTH &f] &4ERROR&7: &c" + message);
                break;
            default:
                send("&f[ &bPlayerBTH &f] &7INFO&f: &b" + message);
                break;
        }
    }

    static void log(Throwable e) {
        try {
            File logsFolder = new File(plugin.getDataFolder() + "/logs");
            logsFolder.mkdirs();
            int logs = 0;
            File[] logFiles = logsFolder.listFiles();
            if (logFiles != null) {
                for (File file : logFiles) {
                    String name = file.getName();
                    if (name.split("-")[0].equals("log")) {
                        logs++;
                    }
                }
            }
            File logFile = new File(plugin.getDataFolder() + "/logs", "log-" + logs + ".log");
            logFile.createNewFile();

            FileWriter writer = new FileWriter(logFile);
            for (StackTraceElement element : e.getStackTrace()) {
                writer.write(element.toString() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Throwable ignore) {}
    }
}
