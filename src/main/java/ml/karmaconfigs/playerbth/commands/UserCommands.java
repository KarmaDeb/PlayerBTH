package ml.karmaconfigs.playerbth.commands;

import java.util.ArrayList;
import java.util.List;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.User;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Days;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.files.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class UserCommands implements CommandExecutor, PlayerBTH, Files {
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      User user = new User(player);
      if (args.length == 0) {
        sendInvalidArgsMessage(player);
      } else if (args[0] != null) {
        switch (args[0].toLowerCase()) {
          case "help":
            if (args.length == 1) {
              sendHelpMessage(player, "help");
            } else if (args.length == 2) {
              String subHelp = args[1];
              sendHelpMessage(player, subHelp);
            }
            break;
          case "setbd":
            if (!user.hasBirthday()) {
              if (args.length == 2) {
                try {
                  int day = Integer.parseInt(args[1].split("/")[0].replaceAll("-", ""));
                  int month = Integer.parseInt(args[1].split("/")[1].replaceAll("-", ""));
                  if (config.usFormat()) {
                    int tmpMonth = day;
                    day = month;
                    month = tmpMonth;
                  }

                  try {
                    Month bdMonth = Month.byID(month);
                    Days days = new Days(bdMonth);
                    if (day <= days.getMax()) {
                      Birthday birthday = new Birthday(bdMonth, day);
                      birthday.setAge(1);
                      user.setBirthday(birthday);
                      user.send(messages.prefix() + messages.birthdaySet(birthday));
                    } else {
                      user.send(messages.prefix() + messages.maxDays(bdMonth, days));
                    }
                  } catch (Throwable e) {
                    user.send(messages.prefix() + messages.invalidMonth());
                  }
                } catch (NumberFormatException e) {
                  user.send(messages.prefix() + messages.incorrectFormat());
                }
              } else if (args.length == 3) {
                try {
                  int day = Integer.parseInt(args[1].split("/")[0].replaceAll("-", ""));
                  int month = Integer.parseInt(args[1].split("/")[1].replaceAll("-", ""));
                  int age = Integer.parseInt(args[2].replaceAll("-", ""));
                  if (age <= 0)
                    age = 1;
                  try {
                    Month bdMonth = Month.byID(month);
                    Days days = new Days(bdMonth);
                    if (day <= days.getMax()) {
                      Birthday birthday = new Birthday(bdMonth, day);
                      birthday.setAge(age);
                      user.setBirthday(birthday);
                      user.send(messages.prefix() + messages.birthdaySet(birthday, age));
                    } else {
                      user.send(messages.prefix() + messages.maxDays(bdMonth, days));
                    }
                  } catch (Throwable e) {
                    user.send(messages.prefix() + messages.invalidMonth());
                  }
                } catch (NumberFormatException e) {
                  user.send(messages.prefix() + messages.incorrectFormat());
                }
              } else {
                sendHelpMessage(player, "setbd");
              }
            } else {
              user.send(messages.prefix() + messages.alreadySet(user.getBirthday()));
            }
            break;
          case "notify":
            if (args.length == 1) {
              user.setNotifications(!user.hasNotifications());
              user.send(messages.prefix() + messages.notification(user.hasNotifications()));
            } else {
              sendHelpMessage(player, "notify");
            }
            break;
          case "private":
            if (args.length == 1) {
              user.setPrivate(!user.isPrivate());
              user.send(messages.prefix() + messages.privacy(user.isPrivate()));
            } else {
              sendHelpMessage(player, "private");
            }
            break;
          default:
            sendInvalidArgsMessage(player);
            break;
        }
      } else {
        sendInvalidArgsMessage(player);
      } 
    } else {
      console.send("This command is for players only", Level.INFO);
    } 
    return false;
  }
  
  private void sendInvalidArgsMessage(Player player) {
    User user = new User(player);
    user.send("&0&m---------------");
    user.send(" ");
    user.send("{0} &aversion {1}", name, version);
    user.send("&7Type &f/bth help&7 for help");
    user.send(" ");
    user.send("&0&m---------------");
  }
  
  private void sendHelpMessage(Player player, String sub) {
    List<String> help;
    User user = new User(player);
    switch (sub) {
      case "help":
        help = new ArrayList<>();
        help.add("&3&m--------------------");
        help.add(" ");
        help.add("&7/bth help <cmd> &f- &7Displays command help");
        if (config.usFormat()) {
          help.add("&7/bth setbd <MM/dd> [age] &f- &7Sets your birthday");
        } else {
          help.add("&7/bth setbd <dd/MM> [age] &f- &7Sets your birthday");
        }
        help.add("&7/bth notify &f- &7Notify other players birthdays");
        help.add("&7/bth private &f- &7Toggle private birthday status");
        help.add(" ");
        help.add("&3&m--------------------");
        user.send(help.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(",", "&r\n&r"));
        return;
      case "setbd":
        if (config.usFormat()) {
          user.send(messages.prefix() + "&7/bth setbd <MM/dd> (Optional)[age] &f- &7Sets your birthday to the specified date with the specified age if set, minimum age is 1");
        } else {
          user.send(messages.prefix() + "&7/bth setbd <dd/MM> (Optional)[age] &f- &7Sets your birthday to the specified date with the specified age if set, minimum age is 1");
        }
        return;
      case "notify":
        user.send(messages.prefix() + "&7/bth notify &f- &7Chose if you want to know about other players birthdays or not");
        return;
      case "private":
        user.send(messages.prefix() + "&7/bth private &f- &7Chose if you want others to be notified about your birthday or keep it private.");
        return;
    } 
    sendInvalidArgsMessage(player);
  }
}
