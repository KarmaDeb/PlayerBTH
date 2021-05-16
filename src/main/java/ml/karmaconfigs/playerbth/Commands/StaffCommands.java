package ml.karmaconfigs.playerbth.commands;

import ml.karmaconfigs.api.bukkit.Console;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.playerbth.api.BirthdayCelebrateEvent;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.DataSys;
import ml.karmaconfigs.playerbth.utils.files.Config;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.files.Messages;
import ml.karmaconfigs.playerbth.utils.mysql.Migration;
import ml.karmaconfigs.playerbth.utils.mysql.SQLPool;
import ml.karmaconfigs.playerbth.utils.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

public class StaffCommands implements CommandExecutor, PlayerBTH, Files {

    private final Permission help = new Permission("playerbirthday.help", PermissionDefault.FALSE);
    private final Permission dump = new Permission("playerbirthday.dump", PermissionDefault.FALSE);
    private final Permission info = new Permission("playerbirthday.info", PermissionDefault.FALSE);
    private final Permission migrate = new Permission("playerbirthday.migrate", PermissionDefault.FALSE);
    private final Permission celebrate = new Permission("playerbirthday.celebrate", PermissionDefault.FALSE);
    private final Permission reload = new Permission("playerbirthday.reload", PermissionDefault.FALSE);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = new User(player);

            if (args.length == 0) {
                sendInvalidArgsMessage(player);
            } else {
                if (args[0] != null) {
                    if (args[0].equals("help")) {
                        if (player.hasPermission(help)) {
                            if (args.length == 1) {
                                sendHelpMessage(player, "help");
                            } else {
                                if (args.length == 2) {
                                    String sub = args[1];
                                    sendHelpMessage(player, sub);
                                }
                            }
                        } else {
                            user.send(messages.prefix() + messages.permission(help));
                        }
                    } else {
                        if (args[0].equals("dump")) {
                            if (player.hasPermission(dump)) {
                                if (args.length == 2) {
                                    UUID uuid;
                                    if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                        uuid = PropertyReader.getUUID(args[1]);
                                    } else {
                                        uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                    }

                                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                    User targetUser = new User(target);

                                    if (targetUser.hasPlayedBefore()) {
                                        targetUser.dumpData();
                                        user.send(messages.prefix() + messages.removed(target));
                                    } else {
                                        user.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                    }
                                } else {
                                    sendHelpMessage(player, "dump");
                                }
                            } else {
                                user.send(messages.prefix() + messages.permission(dump));
                            }
                        } else {
                            if (args[0].equals("info")) {
                                if (player.hasPermission(info)) {
                                    if (args.length == 2) {

                                        UUID uuid;
                                        if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                            uuid = PropertyReader.getUUID(args[1]);
                                        } else {
                                            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                        }

                                        if (plugin.getServer().getOfflinePlayer(uuid).hasPlayedBefore()) {
                                            OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                            User targetUser = new User(target);

                                            user.send("&b&m--------------------");
                                            user.send(" ");
                                            user.send("&7Player: &f" + target.getName());
                                            if (targetUser.hasBirthday()) {
                                                Month month = Month.byID(targetUser.getBirthday().getMonth());
                                                user.send("&7Birthday: &f" + targetUser.getBirthday().getDay() + "&8&l/&f" + month.name().substring(0, 1).toUpperCase() + month.name().substring(1).toLowerCase());
                                                user.send("&7Age: &f" + targetUser.getBirthday().getAge());
                                            } else {
                                                user.send("&7Birthday: &cNot set");
                                            }
                                        } else {
                                            user.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                        }
                                    } else {
                                        sendHelpMessage(player, "info");
                                    }
                                } else {
                                    user.send(messages.prefix() + messages.permission(info));
                                }
                            } else {
                                if (args[0].equals("migrate")) {
                                    if (player.hasPermission(migrate)) {
                                        if (args.length == 1) {
                                            if (config.getDataSystem().equals(DataSys.MYSQL)) {
                                                Migration migration = new Migration();
                                                migration.migrateFromSQLToYaml();

                                                user.send(messages.prefix() + "&aMigrating from MySQL to Yaml, see console for more info");
                                            } else {
                                                try {
                                                    SQLPool pool = new SQLPool(config.mysqlHost(), config.mysqlDatabase(), config.mysqlTable(), config.mysqlUser(), config.mysqlPassword(), config.mysqlPort(), config.useSSL());
                                                    pool.setOptions(config.getMaxConnections(), config.getMinConnections(), config.getConnectionTimeOut(), config.getConnectionLifeTime());

                                                    pool.prepareTables();

                                                    Migration migration = new Migration();
                                                    migration.migrateFromSQLToYaml();

                                                    user.send(messages.prefix() + "&aMigrating from MySQL to Yaml, see console for more info");
                                                } catch (Throwable e) {
                                                    user.send(messages.prefix() + "&cTried to migrate without MySQL data established in config");
                                                }
                                            }
                                        } else {
                                            sendHelpMessage(player, "migrate");
                                        }
                                    } else {
                                        user.send(messages.prefix() + messages.permission(migrate));
                                    }
                                } else {
                                    if (args[0].equals("celebrate")) {
                                        if (player.hasPermission(celebrate)) {
                                            if (args.length == 2) {

                                                UUID uuid;
                                                if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                                    uuid = PropertyReader.getUUID(args[1]);
                                                } else {
                                                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                                }

                                                if (plugin.getServer().getOfflinePlayer(uuid).hasPlayedBefore()) {
                                                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                                    if (target.getPlayer() != null) {
                                                        User targetUser = new User(target);

                                                        BirthdayCelebrateEvent event = new BirthdayCelebrateEvent(target.getPlayer());

                                                        plugin.getServer().getPluginManager().callEvent(event);

                                                        if (targetUser.hasBirthday()) {
                                                            Birthday birthday = targetUser.getBirthday();

                                                            birthday.setAge(birthday.getAge() + 1);
                                                            targetUser.setBirthday(birthday);

                                                            for (Player online : plugin.getServer().getOnlinePlayers()) {
                                                                User targets = new User(online);

                                                                if (online != player) {
                                                                    if (targets.hasNotifications()) {
                                                                        targets.sendTitle(messages.birthdayTitle(target, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(target, targetUser.getBirthday().getAge()));
                                                                        if (config.enableSong()) {
                                                                            targets.playSong(config.getSong());
                                                                        }
                                                                    }
                                                                } else {
                                                                    targetUser.sendTitle(messages.birthdayTitle(target, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(target, targetUser.getBirthday().getAge()));
                                                                    if (config.enableSong()) {
                                                                        targetUser.playSong(config.getSong());
                                                                    }
                                                                }
                                                            }

                                                            if (config.enableFireWorks()) {
                                                                targetUser.spawnFireworks(config.fireworkAmount());
                                                            }

                                                            targetUser.setCelebrated(true);
                                                        } else {
                                                            user.send(messages.prefix() + messages.notSet());
                                                        }
                                                    } else {
                                                        user.send(messages.prefix() + "&cTarget not found");
                                                    }
                                                } else {
                                                    user.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                                }
                                            } else {
                                                sendHelpMessage(player, "celebrate");
                                            }
                                        } else {
                                            user.send(messages.prefix() + messages.permission(celebrate));
                                        }
                                    } else {
                                        if (args[0].equals("reload")) {
                                            if (player.hasPermission(reload)) {
                                                if (Config.manager.reload()) {
                                                    user.send("&f[ &bPlayerBTH &f] &7INFO: &bReloaded config.yml");
                                                } else {
                                                    user.send("&f[ &bPlayerBTH &f] &4ERROR&7: &cCouldn't reload config.yml");
                                                }
                                                if (Messages.manager.reload()) {
                                                    user.send("&f[ &bPlayerBTH &f] &7INFO: &bReloaded messages.yml");
                                                } else {
                                                    user.send("&f[ &bPlayerBTH &f] &4ERROR&7: &cCouldn't reload messages.yml");
                                                }
                                            } else {
                                                user.send(messages.prefix() + messages.permission(reload));
                                            }
                                        } else {
                                            sendInvalidArgsMessage(player);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (args.length == 0) {
                sendInvalidArgsMessage();
            } else {
                if (args[0] != null) {
                    if (args[0].equals("help")) {
                        if (args.length == 1) {
                            sendHelpMessage("help");
                        } else {
                            if (args.length == 2) {
                                String sub = args[1];
                                sendHelpMessage(sub);
                            }
                        }
                    } else {
                        if (args[0].equals("dump")) {
                            if (args.length == 2) {

                                UUID uuid;
                                if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                    uuid = PropertyReader.getUUID(args[1]);
                                } else {
                                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                }

                                OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                User targetUser = new User(target);

                                if (targetUser.hasPlayedBefore()) {
                                    targetUser.dumpData();
                                    Console.send(messages.prefix() + messages.removed(target));
                                } else {
                                    Console.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                }
                            } else {
                                sendHelpMessage("dump");
                            }
                        } else {
                            if (args[0].equals("info")) {
                                if (args.length == 2) {

                                    UUID uuid;
                                    if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                        uuid = PropertyReader.getUUID(args[1]);
                                    } else {
                                        uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                    }

                                    if (plugin.getServer().getOfflinePlayer(uuid).hasPlayedBefore()) {
                                        OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                        User targetUser = new User(target);

                                        Console.send("&b&m--------------------");
                                        Console.send(" ");
                                        Console.send("&7Player: &f" + target.getName());
                                        if (targetUser.hasBirthday()) {
                                            Month month = Month.byID(targetUser.getBirthday().getMonth());
                                            Console.send("&7Birthday: &f" + targetUser.getBirthday().getDay() + "&8&l/&f" + month.name().substring(0, 1).toUpperCase() + month.name().substring(1).toLowerCase());
                                            Console.send("&7Age: &f" + targetUser.getBirthday().getAge());
                                        } else {
                                            Console.send("&7Birthday: &cNot set");
                                        }
                                    } else {
                                        Console.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                    }
                                } else {
                                    sendHelpMessage("info");
                                }
                            } else {
                                if (args[0].equals("migrate")) {
                                    if (args.length == 1) {
                                        if (config.getDataSystem().equals(DataSys.MYSQL)) {
                                            Migration migration = new Migration();
                                            migration.migrateFromSQLToYaml();

                                            Console.send(messages.prefix() + "&aMigrating from MySQL to Yaml");
                                        } else {
                                            try {
                                                SQLPool pool = new SQLPool(config.mysqlHost(), config.mysqlDatabase(), config.mysqlTable(), config.mysqlUser(), config.mysqlPassword(), config.mysqlPort(), config.useSSL());
                                                pool.setOptions(config.getMaxConnections(), config.getMinConnections(), config.getConnectionTimeOut(), config.getConnectionLifeTime());

                                                pool.prepareTables();

                                                Migration migration = new Migration();
                                                migration.migrateFromSQLToYaml();

                                                Console.send(messages.prefix() + "&aMigrating from MySQL to Yaml");
                                            } catch (Throwable e) {
                                                Console.send(messages.prefix() + "&cTried to migrate without MySQL data established in config");
                                            }
                                        }
                                    } else {
                                        sendHelpMessage("migrate");
                                    }
                                } else {
                                    if (args[0].equals("celebrate")) {
                                        if (args.length == 2) {

                                            UUID uuid;
                                            if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                                                uuid = PropertyReader.getUUID(args[1]);
                                            } else {
                                                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[1]).getBytes(StandardCharsets.UTF_8));
                                            }

                                            if (plugin.getServer().getOfflinePlayer(uuid).hasPlayedBefore()) {
                                                OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
                                                if (target.getPlayer() != null) {
                                                    User targetUser = new User(target);

                                                    BirthdayCelebrateEvent event = new BirthdayCelebrateEvent(target.getPlayer());
                                                    plugin.getServer().getPluginManager().callEvent(event);

                                                    if (targetUser.hasBirthday()) {
                                                        Birthday birthday = targetUser.getBirthday();
                                                        for (Player online : plugin.getServer().getOnlinePlayers()) {
                                                            User targets = new User(online);

                                                            if (online != target.getPlayer()) {
                                                                if (targets.hasNotifications()) {
                                                                    targets.sendTitle(messages.birthdayTitle(target, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(target, targetUser.getBirthday().getAge()));
                                                                    if (config.enableSong()) {
                                                                        targets.playSong(config.getSong());
                                                                    }
                                                                }
                                                            } else {
                                                                targetUser.sendTitle(messages.birthdayTitle(target, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(target, targetUser.getBirthday().getAge()));
                                                                if (config.enableSong()) {
                                                                    targetUser.playSong(config.getSong());
                                                                }
                                                            }
                                                        }

                                                        birthday.setAge(birthday.getAge() + 1);
                                                        targetUser.setBirthday(birthday);

                                                        if (config.enableFireWorks()) {
                                                            targetUser.spawnFireworks(config.fireworkAmount());
                                                        }

                                                        targetUser.setCelebrated(true);
                                                    } else {
                                                        Console.send(messages.prefix() + messages.targetNotSet(target.getPlayer()));
                                                    }
                                                } else {
                                                    Console.send(messages.prefix() + "&cTarget not found");
                                                }
                                            } else {
                                                Console.send(messages.prefix() + messages.unknownPlayer(args[1]));
                                            }
                                        } else {
                                            sendHelpMessage("celebrate");
                                        }
                                    } else {
                                        if (args[0].equals("reload")) {
                                            if (Config.manager.reload()) {
                                                Console.send(plugin, "Reloaded config.yml", Level.OK);
                                            } else {
                                                Console.send(plugin, "Couldn't reload config.yml", Level.GRAVE);
                                            }
                                            if (Messages.manager.reload()) {
                                                Console.send(plugin, "Reloaded messages.yml", Level.OK);
                                            } else {
                                                Console.send(plugin, "Couldn't reload messages.yml", Level.GRAVE);
                                            }
                                        } else {
                                            sendInvalidArgsMessage();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void sendInvalidArgsMessage() {
        Console.send("&0&m---------------");
        Console.send(" ");
        Console.send("{0} &aversion {1}", name, version);
        Console.send("&7Type &f/bths help &7 for help");
        Console.send(" ");
        Console.send("&0&m---------------");
    }

    private void sendInvalidArgsMessage(Player player) {
        User user = new User(player);

        user.send("&0&m---------------");
        user.send(" ");
        user.send("{0} &aversion {1}", name, version);
        user.send("&7Type &f/bths help&7 for help");
        user.send(" ");
        user.send("&0&m---------------");
    }

    private void sendHelpMessage(String sub) {
        switch (sub.toLowerCase()) {
            case "help":
                List<String> help = new ArrayList<>();
                help.add("&3&m--------------------");
                help.add(" ");
                help.add("&7/bths help <cmd> &f- &7Displays command help");
                help.add("&7/bths dump <player> &f- &7Removes user data");
                help.add("&7/bths info <player> &f- &7Displays user info");
                help.add("&7/bths celebrate <player> &f- &7Celebrates the birthday");
                help.add("&7/bths migrate &f- &7Migrates data from MySQL");
                help.add(" ");
                help.add("&3&m--------------------");
                Console.send(help.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "&r\n&r"));
                break;
            case "dump":
                Console.send(messages.prefix() +"&7/bths dump <player> &f- &7Removes player birthday data, including age and notification configuration");
                break;
            case "info":
                Console.send(messages.prefix() +"&7/bths info <player> &f- &7Shows player birthday info, age, and notification configuration");
                break;
            case "migrate":
                Console.send(messages.prefix() +"&7/bths migrate &f- &7Migrates all players data from MySQL to Yaml");
                break;
            case "celebrate":
                Console.send(messages.prefix() +"&7/bths celebrate <player> &f- &7Forces the plugin to celebrate the specified player birthday");
                break;
            default:
                sendInvalidArgsMessage();
        }
    }

    private void sendHelpMessage(Player player, String sub) {
        User user = new User(player);
        switch (sub.toLowerCase()) {
            case "help":
                List<String> help = new ArrayList<>();
                help.add("&3&m--------------------");
                help.add(" ");
                help.add("&7/bths help <cmd> &f- &7Displays command help");
                help.add("&7/bths dump <player> &f- &7Removes user data");
                help.add("&7/bths info <player> &f- &7Displays user info");
                help.add("&7/bths celebrate <player> &f- &7Celebrates the birthday");
                help.add("&7/bths migrate &f- &7Migrates data from MySQL");
                help.add(" ");
                help.add("&3&m--------------------");
                user.send(help.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "&r\n&r"));
                break;
            case "dump":
                user.send(messages.prefix() + "&7/bth dump <player> &f- &7Removes player birthday data, including age and notification configuration");
                break;
            case "info":
                user.send(messages.prefix() +"&7/bth info <player> &f- &7Shows player birthday info, age, and notification configuration");
                break;
            case "migrate":
                user.send(messages.prefix() +"&7/bth migrate &f- &7Migrates all players data from MySQL to Yaml");
                break;
            case "celebrate":
                user.send(messages.prefix() +"&7/bths celebrate <player> &f- &7Forces the plugin to celebrate the specified player birthday");
                break;
            default:
                sendInvalidArgsMessage(player);
        }
    }
}

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

final class PropertyReader implements PlayerBTH {

    public static Object getProperty(String path) {
        Object value = "";

        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(plugin.getServer().getWorldContainer(), "server.properties")));

            value = prop.getProperty(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static UUID getUUID(String player) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + player);
            InputStream in = url.openStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            return UUID.fromString(json.getString("id").replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            ));
        } catch (Exception | Error e) {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}