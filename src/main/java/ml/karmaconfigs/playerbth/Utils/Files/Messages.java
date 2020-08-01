package ml.karmaconfigs.playerbth.Utils.Files;

import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.Birthday.Days;
import ml.karmaconfigs.playerbth.Utils.Birthday.Month;
import ml.karmaconfigs.playerbth.Utils.Server;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.HashMap;

public final class Messages implements PlayerBTH {

    private final File file = new File(plugin.getDataFolder(), "messages.yml");
    private final FileConfiguration messages = YamlConfiguration.loadConfiguration(file);

    public Messages() {
        try {
            if (messages.getInt("Ver") != 2) {
                File backupsFolder = new File(plugin.getDataFolder() + "/backups");
                if (!backupsFolder.exists()) {
                    if (backupsFolder.mkdirs()) {
                        Server.send("Created backups folder", Server.AlertLevel.INFO);
                    }
                }


                int old = 0;
                File[] files = backupsFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (name.contains("_")) {
                            if (name.split("_")[0].equals("messages-old")) {
                                old++;
                            }
                        }
                    }
                }

                int amount = old + 1;
                String amountStr;
                if (amount <= 9) {
                    amountStr = "0" + amount;
                } else {
                    amountStr = String.valueOf(amount);
                }

                File newMessages = new File(plugin.getDataFolder() + "/backups", "messages-old_" + amountStr + ".yml");
                String path = newMessages.getPath().replaceAll("\\\\", "/");

                if (file.renameTo(newMessages)) {
                    Server.send("Updated messages.yml, have been renamed to " + path, Server.AlertLevel.WARNING);
                }

                YamlCreator creator = new YamlCreator("messages.yml", true);
                creator.createFile();
                creator.setDefaults();
                creator.saveFile();

                Files.copyValues(newMessages, creator.getFile(), "Ver");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        YamlCreator creator = new YamlCreator("messages.yml", true);
        creator.createFile();
        creator.setDefaults();
        creator.saveFile();
    }

    private String get(String path) {
        return messages.getString(path);
    }

    private String get(String path, HashMap<String, Object> replaces) {
        String str = get(path);

        for (String arg : replaces.keySet()) {
            str = str.replace(arg, replaces.get(arg).toString());
        }

        return str;
    }

    public final String prefix() {
        return get("Prefix");
    }

    public final String birthdaySet(Birthday birthday) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{day}", birthday.getDay());
        switch (Month.byID(birthday.getMonth())) {
            case January:
                replaces.put("{month}", january());
                break;
            case February:
                replaces.put("{mont}", february());
                break;
            case March:
                replaces.put("{month}", march());
                break;
            case April:
                replaces.put("{month}", april());
                break;
            case May:
                replaces.put("{month}", may());
                break;
            case June:
                replaces.put("{month}", june());
                break;
            case July:
                replaces.put("{month}", july());
                break;
            case August:
                replaces.put("{month}", august());
                break;
            case September:
                replaces.put("{month}", september());
                break;
            case October:
                replaces.put("{month}", october());
                break;
            case November:
                replaces.put("{month}", november());
                break;
            case December:
                replaces.put("{month}", december());
                break;
        }

        return get("Set", replaces);
    }

    public final String birthdaySet(Birthday birthday, int age) {
        Month month = Month.byID(birthday.getMonth());
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{day}", birthday.getDay());
        switch (month) {
            case January:
                replaces.put("{month}", january());
                break;
            case February:
                replaces.put("{mont}", february());
                break;
            case March:
                replaces.put("{month}", march());
                break;
            case April:
                replaces.put("{month}", april());
                break;
            case May:
                replaces.put("{month}", may());
                break;
            case June:
                replaces.put("{month}", june());
                break;
            case July:
                replaces.put("{month}", july());
                break;
            case August:
                replaces.put("{month}", august());
                break;
            case September:
                replaces.put("{month}", september());
                break;
            case October:
                replaces.put("{month}", october());
                break;
            case November:
                replaces.put("{month}", november());
                break;
            case December:
                replaces.put("{month}", december());
                break;
        }
        replaces.put("{age}", age);

        return get("SetAge", replaces);
    }

    public final String alreadySet(Birthday birthday) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{day}", birthday.getDay());
        switch (Month.byID(birthday.getMonth())) {
            case January:
                replaces.put("{month}", january());
                break;
            case February:
                replaces.put("{mont}", february());
                break;
            case March:
                replaces.put("{month}", march());
                break;
            case April:
                replaces.put("{month}", april());
                break;
            case May:
                replaces.put("{month}", may());
                break;
            case June:
                replaces.put("{month}", june());
                break;
            case July:
                replaces.put("{month}", july());
                break;
            case August:
                replaces.put("{month}", august());
                break;
            case September:
                replaces.put("{month}", september());
                break;
            case October:
                replaces.put("{month}", october());
                break;
            case November:
                replaces.put("{month}", november());
                break;
            case December:
                replaces.put("{month}", december());
                break;
        }

        return get("AlreadySet", replaces);
    }

    public final String notSet() {
        return get("NotSet");
    }

    public final String minAge() {
        return get("MinAge");
    }

    public final String removed(OfflinePlayer player) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{player}", player.getName());

        return get("Removed", replaces);
    }

    public final String cantRemove(OfflinePlayer player) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{player}", player.getName());

        return get("CantRemove", replaces);
    }

    public final String specifyPlayer() {
        return get("SpecifyPlayer");
    }

    public final String notification(boolean value) {
        HashMap<String, Object> replaces = new HashMap<>();
        if (value) {
            replaces.put("{status}", enabled());
        } else {
            replaces.put("{status}", disabled());
        }

        return get("Notification", replaces);
    }

    public final String invalidMonth() {
        return get("InvalidMonth");
    }

    public final String maxDays(Month month, Days days) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{days}", days.getMax());
        switch (month) {
            case January:
                replaces.put("{month}", january());
                break;
            case February:
                replaces.put("{mont}", february());
                break;
            case March:
                replaces.put("{month}", march());
                break;
            case April:
                replaces.put("{month}", april());
                break;
            case May:
                replaces.put("{month}", may());
                break;
            case June:
                replaces.put("{month}", june());
                break;
            case July:
                replaces.put("{month}", july());
                break;
            case August:
                replaces.put("{month}", august());
                break;
            case September:
                replaces.put("{month}", september());
                break;
            case October:
                replaces.put("{month}", october());
                break;
            case November:
                replaces.put("{month}", november());
                break;
            case December:
                replaces.put("{month}", december());
                break;
        }

        return get("MaxDays", replaces);
    }

    public final String incorrectFormat() {
        return get("IncorrectFormat");
    }

    public final String unknownPlayer(String player) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{player}", player);

        return get("UnknownPlayer", replaces);
    }

    public final String unknown() {
        return get("Unknown");
    }

    public final String permission(Permission permission) {
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{permission}", permission.getName());

        return get("Permission", replaces);
    }

    public final String birthdayTitle(OfflinePlayer player, int age) {
        age = age + 1;
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{player}", player.getName());

        String format;
        if (Math.abs(age) % 10 == 1) {
            format = "st";
        } else {
            if (Math.abs(age) % 10 == 2) {
                format = "nd";
            } else {
                if (Math.abs(age) % 10 == 3) {
                    format = "rd";
                } else {
                    format = "th";
                }
            }
        }
        replaces.put("{age}", age + format);

        return get("Birthdays.Title", replaces);
    }

    public final String birthdaySubtitle(OfflinePlayer player, int age) {
        age = age + 1;
        HashMap<String, Object> replaces = new HashMap<>();
        replaces.put("{player}", player.getName());

        String format;
        if (Math.abs(age) % 10 == 1) {
            format = "st";
        } else {
            if (Math.abs(age) % 10 == 2) {
                format = "nd";
            } else {
                if (Math.abs(age) % 10 == 3) {
                    format = "rd";
                } else {
                    format = "th";
                }
            }
        }
        replaces.put("{age}", age + format);

        return get("Birthdays.Subtitle", replaces);
    }

    public final String january() { return get("Months.January"); }

    public final String february() {
        return get("Months.February");
    }

    public final String march() {
        return get("Months.March");
    }

    public final String april() {
        return get("Months.April");
    }

    public final String may() {
        return get("Months.May");
    }

    public final String june() {
        return get("Months.June");
    }

    public final String july() {
        return get("Months.July");
    }

    public final String august() {
        return get("Months.August");
    }

    public final String september() {
        return get("Months.September");
    }

    public final String october() {
        return get("Months.October");
    }

    public final String november() {
        return get("Months.November");
    }

    public final String december() {
        return get("Months.December");
    }

    public final String enabled() { return get("Notifications.Enabled"); }

    public final String disabled() { return get("Notifications.Disabled"); }
}
