package ml.karmaconfigs.playerbth.utils.birthday;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.karmaconfigs.playerbth.utils.User;
import ml.karmaconfigs.playerbth.utils.files.Files;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerBTHExpansion extends PlaceholderExpansion implements Files {
  public boolean canRegister() {
    return true;
  }
  
  public boolean persist() {
    return true;
  }
  
  @NotNull
  public String getIdentifier() {
    return "birthday";
  }
  
  @NotNull
  public String getAuthor() {
    return "KarmaDev";
  }
  
  @NotNull
  public String getVersion() {
    return "1.0.0";
  }
  
  public String onRequest(OfflinePlayer player, @NotNull String identifier) {
    if (player.getPlayer() != null) {
      User user = new User(player);
      switch (identifier.toLowerCase()) {
        case "set":
          return String.valueOf(user.hasBirthday()).replace("true", config.birthdaySet()).replace("false", config.birthdayNotSet());
        case "timeleft":
          if (user.hasBirthday())
            return user.getBirthday().getTimeLeft().split(" ")[1]; 
          return config.birthdayNotSet();
        case "daysleft":
          if (user.hasBirthday())
            return user.getBirthday().getTimeLeft().split(" ")[0]; 
          return config.birthdayNotSet();
        case "date":
          if (user.hasBirthday())
            return user.getBirthday().getDay() + "/" + user.getBirthday().getMonth(); 
          return config.birthdayNotSet();
        case "day":
          if (user.hasBirthday())
            return String.valueOf(user.getBirthday().getDay()); 
          return config.birthdayNotSet();
        case "dayname":
          if (user.hasBirthday())
            return user.getBirthday().dayName(player.getPlayer().getLocale()).substring(0, 1).toUpperCase() + user.getBirthday().dayName(player.getPlayer().getLocale()).substring(1).toLowerCase(); 
          return config.birthdayNotSet();
        case "month":
          if (user.hasBirthday())
            return String.valueOf(user.getBirthday().getMonth()); 
          return config.birthdayNotSet();
        case "monthname":
          if (user.hasBirthday())
            return user.getBirthday().monthName(player.getPlayer().getLocale()).substring(0, 1).toUpperCase() + user.getBirthday().monthName(player.getPlayer().getLocale()).substring(1).toLowerCase(); 
          return config.birthdayNotSet();
        case "age":
          if (user.hasBirthday())
            return String.valueOf(user.getBirthday().getAge()); 
          return config.birthdayNotSet();
      } 
      return "Unknown: " + identifier;
    } 
    return "player not online";
  }
}
