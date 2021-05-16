package ml.karmaconfigs.playerbth.utils.files;

import ml.karmaconfigs.api.bukkit.karmayaml.FileCopy;
import ml.karmaconfigs.api.bukkit.karmayaml.YamlReloader;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Days;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.HashMap;

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

public final class Messages implements PlayerBTH {

    private final static File file = new File(plugin.getDataFolder(), "messages.yml");
    private final static FileConfiguration messages = YamlConfiguration.loadConfiguration(file);

    public interface manager {

        static boolean reload() {
            try {
                YamlReloader reloader = new YamlReloader(plugin, file, "messages.yml");
                if (reloader.reloadAndCopy()) {
                    messages.loadFromString(reloader.getYamlString());
                    return true;
                }
            } catch (Throwable ex) {
                try {
                    FileCopy copy = new FileCopy(plugin, "messages.yml");
                    copy.copy(file);

                    return true;
                } catch (Throwable ignored) {}
            }
            return false;
        }
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

    public final String targetNotSet(final Player player) {
        return get("TargetNotSet").replace("{player}", StringUtils.stripColor(player.getDisplayName()));
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
