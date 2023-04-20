package ml.karmaconfigs.playerbth.utils.files;

import java.io.File;
import java.util.HashMap;

import ml.karmaconfigs.api.common.karma.file.yaml.KarmaYamlManager;
import ml.karmaconfigs.api.common.karma.file.yaml.YamlReloader;
import ml.karmaconfigs.api.common.string.StringUtils;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Days;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

public final class Messages implements PlayerBTH {
  private static final File file = new File(plugin.getDataFolder(), "messages.yml");
  
  private static final KarmaYamlManager messages = new KarmaYamlManager(file);
  
  public static interface manager {
    static boolean reload() {
      YamlReloader reloader = Messages.messages.getReloader();
      if (reloader != null) {
        reloader.reload();
        return true;
      } 
      return false;
    }
  }
  
  private String get(String path) {
    return messages.getString(path);
  }
  
  private String get(String path, HashMap<String, Object> replaces) {
    String str = get(path);
    for (String arg : replaces.keySet())
      str = str.replace(arg, replaces.get(arg).toString()); 
    return str;
  }
  
  public final String prefix() {
    return get("Prefix");
  }
  
  public final String birthdaySet(Birthday birthday) {
    HashMap<String, Object> replaces = new HashMap<>();
    replaces.put("{day}", Integer.valueOf(birthday.getDay()));
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
    replaces.put("{day}", Integer.valueOf(birthday.getDay()));
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
    replaces.put("{age}", Integer.valueOf(age));
    return get("SetAge", replaces);
  }
  
  public final String alreadySet(Birthday birthday) {
    HashMap<String, Object> replaces = new HashMap<>();
    replaces.put("{day}", Integer.valueOf(birthday.getDay()));
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
  
  public final String targetNotSet(OfflinePlayer player) {
    String name = player.getName();
    return get("TargetNotSet").replace("{player}", StringUtils.stripColor((name != null) ? name : player.getUniqueId().toString()));
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
      replaces.put("{status}", notiEnabled());
    } else {
      replaces.put("{status}", notiDisabled());
    }

    return get("Notification", replaces);
  }

  public String privacy(final boolean value) {
    HashMap<String, Object> replaces = new HashMap<>();
    if (value) {
      replaces.put("{status}", privEnabled());
    } else {
      replaces.put("{status}", privDisabled());
    }
    return get("Privacy", replaces);
  }

  public final String invalidMonth() {
    return get("InvalidMonth");
  }
  
  public final String maxDays(Month month, Days days) {
    HashMap<String, Object> replaces = new HashMap<>();
    replaces.put("{days}", Integer.valueOf(days.getMax()));
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
    String format;
    HashMap<String, Object> replaces = new HashMap<>();
    replaces.put("{player}", player.getName());
    if (Math.abs(age) % 10 == 1) {
      format = "st";
    } else if (Math.abs(age) % 10 == 2) {
      format = "nd";
    } else if (Math.abs(age) % 10 == 3) {
      format = "rd";
    } else {
      format = "th";
    } 
    replaces.put("{age}", age + format);
    return get("Birthdays.Title", replaces);
  }
  
  public final String birthdaySubtitle(OfflinePlayer player, int age) {
    String format;
    age++;
    HashMap<String, Object> replaces = new HashMap<>();
    replaces.put("{player}", player.getName());
    if (Math.abs(age) % 10 == 1) {
      format = "st";
    } else if (Math.abs(age) % 10 == 2) {
      format = "nd";
    } else if (Math.abs(age) % 10 == 3) {
      format = "rd";
    } else {
      format = "th";
    } 
    replaces.put("{age}", age + format);
    return get("Birthdays.Subtitle", replaces);
  }
  
  public final String january() {
    return get("Months.January");
  }
  
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
  
  public final String notiEnabled() {
    return get("Notifications.Enabled");
  }
  
  public final String notiDisabled() {
    return get("Notifications.Disabled");
  }

  public final String privEnabled() {
    return get("Private.Enabled");
  }

  public final String privDisabled() {
    return get("Private.Disabled");
  }
}
