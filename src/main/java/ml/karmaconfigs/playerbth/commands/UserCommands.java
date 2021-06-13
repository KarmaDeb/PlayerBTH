package ml.karmaconfigs.playerbth.commands;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Days;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

public final class UserCommands implements CommandExecutor, PlayerBTH, Files {

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = new User(player);

            if (args.length == 0) {
                sendInvalidArgsMessage(player);
            } else {
                if (args[0] != null) {
                    if (args[0].equalsIgnoreCase("help")) {
                        if (args.length == 1) {
                            sendHelpMessage(player, "help");
                        } else {
                            if (args.length == 2) {
                                String subHelp = args[1];
                                sendHelpMessage(player, subHelp);
                            }
                        }
                    } else {
                        if (args[0].equalsIgnoreCase("setbd")) {
                            if (!user.hasBirthday()) {
                                if (args.length == 2) {
                                    try {
                                        int day;
                                        int month;

                                        day = Integer.parseInt(args[1].split("/")[0].replaceAll("-", ""));
                                        month = Integer.parseInt(args[1].split("/")[1].replaceAll("-", ""));

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
                                } else {
                                    if (args.length == 3) {
                                        try {
                                            int day;
                                            int month;
                                            int age;

                                            day = Integer.parseInt(args[1].split("/")[0].replaceAll("-", ""));
                                            month = Integer.parseInt(args[1].split("/")[1].replaceAll("-", ""));
                                            age = Integer.parseInt(args[2].replaceAll("-", ""));

                                            if (age <= 0) {
                                                age = 1;
                                            }

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
                                }
                            } else {
                                user.send(messages.prefix() + messages.alreadySet(user.getBirthday()));
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("notify")) {
                                if (args.length == 1) {
                                    if (user.hasNotifications()) {
                                        user.setNotifications(false);
                                        user.send(messages.prefix() + messages.notification(false));
                                    } else {
                                        user.setNotifications(true);
                                        user.send(messages.prefix() + messages.notification(true));
                                    }
                                } else {
                                    sendHelpMessage(player, "notify");
                                }
                            } else {
                                sendInvalidArgsMessage(player);
                            }
                        }
                    }
                } else {
                    sendInvalidArgsMessage(player);
                }
            }
        } else {
            Console.send(plugin, "This command is for players only", Level.INFO);
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
        User user = new User(player);
        switch (sub) {
            case "help":
                List<String> help = new ArrayList<>();
                help.add("&3&m--------------------");
                help.add(" ");
                help.add("&7/bth help <cmd> &f- &7Displays command help");
                help.add("&7/bth setbd <dd/MM> [age] &f- &7Sets your birthday");
                help.add("&7/bth notify &f- &7Notify other players birthdays");
                help.add(" ");
                help.add("&3&m--------------------");
                user.send(help.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "&r\n&r"));
                break;
            case "setbd":
                user.send(messages.prefix() +"&7/bth setbd <dd/MM> (Optional)[age] &f- &7Sets your birthday to the specified date with the specified age if set, minimum age is 1");
                break;
            case "notify":
                user.send(messages.prefix() +"&7/bth notify &f- &7Chose if you want to know about other players birthdays or not");
                break;
            default:
                sendInvalidArgsMessage(player);
        }
    }
}
