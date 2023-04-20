package ml.karmaconfigs.playerbth.commands;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.DataSys;
import ml.karmaconfigs.playerbth.utils.User;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.files.Config;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.files.Messages;
import ml.karmaconfigs.playerbth.utils.mysql.Migration;
import ml.karmaconfigs.playerbth.utils.mysql.SQLPool;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class StaffCommands implements CommandExecutor, PlayerBTH, Files {
  private final Permission help = new Permission("playerbirthday.help", PermissionDefault.FALSE);
  
  private final Permission dump = new Permission("playerbirthday.dump", PermissionDefault.FALSE);
  
  private final Permission info = new Permission("playerbirthday.info", PermissionDefault.FALSE);
  
  private final Permission migrate = new Permission("playerbirthday.migrate", PermissionDefault.FALSE);
  
  private final Permission celebrate = new Permission("playerbirthday.celebrate", PermissionDefault.FALSE);
  
  private final Permission reload = new Permission("playerbirthday.reload", PermissionDefault.FALSE);
  
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      User user = new User(player);

      if (args.length == 0) {
        sendInvalidArgsMessage(player);
      } else {
        switch (args[0].toLowerCase()) {
          case "help":
            if (player.hasPermission(this.help)) {
              if (args.length == 1) {
                sendHelpMessage(player, "help");
              } else if (args.length == 2) {
                String sub = args[1];
                sendHelpMessage(player, sub);
              }
            } else {
              user.send(messages.prefix() + messages.permission(this.help));
            }
            break;
          case "dump":
            if (player.hasPermission(this.dump)) {
              if (args.length == 2) {
                String name = args[1];
                OfflinePlayer player1 = plugin.getServer().getPlayer(name);
                if (player1 == null) {
                  UUID uuid;
                  if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                    uuid = PropertyReader.getUUID(name);
                  } else {
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                  }

                  player1 = plugin.getServer().getOfflinePlayer(uuid);
                }

                User targetUser = new User(player1);
                if (targetUser.hasPlayedBefore()) {
                  targetUser.dumpData();
                  user.send(messages.prefix() + messages.removed(player1));
                } else {
                  user.send(messages.prefix() + messages.unknownPlayer(args[1]));
                }
              } else {
                sendHelpMessage(player, "dump");
              }
            } else {
              user.send(messages.prefix() + messages.permission(this.dump));
            }
            break;
          case "info":
            if (player.hasPermission(this.info)) {
              if (args.length == 2) {
                String name = args[1];
                OfflinePlayer player1 = plugin.getServer().getPlayer(name);
                if (player1 == null) {
                  UUID uuid;
                  if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                    uuid = PropertyReader.getUUID(name);
                  } else {
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                  }

                  player1 = plugin.getServer().getOfflinePlayer(uuid);
                }

                User targetUser = new User(player1);
                user.send("&b&m--------------------");
                user.send(" ");
                user.send("&7Player: &f" + player1.getName());
                if (targetUser.hasBirthday()) {
                  Month month = Month.byID(targetUser.getBirthday().getMonth());
                  user.send("&7Birthday: &f" + targetUser.getBirthday().getDay() + "&8&l/&f" + month.name().substring(0, 1).toUpperCase() + month.name().substring(1).toLowerCase());
                  user.send("&7Age: &f" + targetUser.getBirthday().getAge());
                } else {
                  user.send("&7Birthday: &cNot set");
                }
              } else {
                sendHelpMessage(player, "info");
              }
            } else {
              user.send(messages.prefix() + messages.permission(this.info));
            }
            break;
          case "migrate":
            if (player.hasPermission(this.migrate)) {
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
              user.send(messages.prefix() + messages.permission(this.migrate));
            }
            break;
          case "celebrate":
            if (player.hasPermission(this.celebrate)) {
              if (args.length == 2) {
                String name = args[1];
                OfflinePlayer player1 = plugin.getServer().getPlayer(name);
                if (player1 == null) {
                  UUID uuid;
                  if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                    uuid = PropertyReader.getUUID(name);
                  } else {
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                  }
                  player1 = plugin.getServer().getOfflinePlayer(uuid);
                }

                User targetUser = new User(player1);
                if (targetUser.hasPlayedBefore()) {
                  if (targetUser.hasBirthday()) {
                    Birthday birthday = targetUser.getBirthday();
                    birthday.setAge(birthday.getAge() + 1);
                    targetUser.setBirthday(birthday);
                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                      User targets = new User(online);
                      if (online != player1.getPlayer()) {
                        if (targets.hasNotifications()) {
                          targets.sendTitle(messages.birthdayTitle(player1, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(player1, targetUser.getBirthday().getAge()));
                          if (config.enableSong())
                            targets.playSong(config.getSong());
                        }
                        continue;
                      }
                      targetUser.sendTitle(messages.birthdayTitle(player1, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(player1, targetUser.getBirthday().getAge()));
                      if (config.enableSong())
                        targetUser.playSong(config.getSong());
                    }
                    if (config.enableFireWorks())
                      targetUser.spawnFireworks(config.fireworkAmount());
                    targetUser.setCelebrated(true);
                    user.send(messages.prefix() + "&aBirthday of " + name + " celebrated!");
                  } else {
                    user.send(messages.prefix() + messages.targetNotSet(player1));
                  }
                } else {
                  user.send(messages.prefix() + messages.unknownPlayer(name));
                }
              } else {
                sendHelpMessage(player, "celebrate");
              }
            } else {
              user.send(messages.prefix() + messages.permission(this.celebrate));
            }
            break;
          case "reload":
            if (player.hasPermission(this.reload)) {
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
              user.send(messages.prefix() + messages.permission(this.reload));
            }
            break;
          default:
            sendInvalidArgsMessage(player);
            break;
        }
      } 
    } else if (args.length == 0) {
      sendInvalidArgsMessage();
    } else if (args[0] != null) {
      switch (args[0]) {
        case "help":
          if (args.length == 1) {
            sendHelpMessage("help");
          } else if (args.length == 2) {
            String sub = args[1];
            sendHelpMessage(sub);
          }
          break;
        case "dump":
          if (args.length == 2) {
            String name = args[1];
            OfflinePlayer player = plugin.getServer().getPlayer(name);
            if (player == null || !player.isOnline()) {
              UUID uuid;
              if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                uuid = PropertyReader.getUUID(name);
              } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
              }
              player = plugin.getServer().getOfflinePlayer(uuid);
            }

            User targetUser = new User(player);
            if (targetUser.hasPlayedBefore()) {
              targetUser.dumpData();
              console.send(messages.prefix() + messages.removed(player));
            } else {
              console.send(messages.prefix() + messages.unknownPlayer(name));
            }
          } else {
            sendHelpMessage("dump");
          }
          break;
        case "info":
          if (args.length == 2) {
            String name = args[1];
            OfflinePlayer player = plugin.getServer().getPlayer(name);
            if (player == null) {
              UUID uuid;
              if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                uuid = PropertyReader.getUUID(name);
              } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
              }
              player = plugin.getServer().getOfflinePlayer(uuid);
            }

            if (player.hasPlayedBefore()) {
              User targetUser = new User(player);
              console.send("&b&m--------------------");
              console.send(" ");
              console.send("&7Player: &f" + player.getName());
              if (targetUser.hasBirthday()) {
                Month month = Month.byID(targetUser.getBirthday().getMonth());
                console.send("&7Birthday: &f" + targetUser.getBirthday().getDay() + "&8&l/&f" + month.name().substring(0, 1).toUpperCase() + month.name().substring(1).toLowerCase());
                console.send("&7Age: &f" + targetUser.getBirthday().getAge());
              } else {
                console.send("&7Birthday: &cNot set");
              }
            } else {
              console.send(messages.prefix() + messages.unknownPlayer(args[1]));
            }
          } else {
            sendHelpMessage("info");
          }
          break;
        case "migrate":
          if (args.length == 1) {
            if (config.getDataSystem().equals(DataSys.MYSQL)) {
              Migration migration = new Migration();
              migration.migrateFromSQLToYaml();
              console.send(messages.prefix() + "&aMigrating from MySQL to Yaml");
            } else {
              try {
                SQLPool pool = new SQLPool(config.mysqlHost(), config.mysqlDatabase(), config.mysqlTable(), config.mysqlUser(), config.mysqlPassword(), config.mysqlPort(), config.useSSL());
                pool.setOptions(config.getMaxConnections(), config.getMinConnections(), config.getConnectionTimeOut(), config.getConnectionLifeTime());
                pool.prepareTables();
                Migration migration = new Migration();
                migration.migrateFromSQLToYaml();
                console.send(messages.prefix() + "&aMigrating from MySQL to Yaml");
              } catch (Throwable e) {
                console.send(messages.prefix() + "&cTried to migrate without MySQL data established in config");
              }
            }
          } else {
            sendHelpMessage("migrate");
          }
          break;
        case "celebrate":
          if (args.length == 2) {
            String name = args[1];
            OfflinePlayer player = plugin.getServer().getPlayer(name);
            if (player == null) {
              UUID uuid;
              if (PropertyReader.getProperty("online-mode").toString().equals("true")) {
                uuid = PropertyReader.getUUID(name);
              } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
              }

              player = plugin.getServer().getOfflinePlayer(uuid);
            }

            User targetUser = new User(player);
            if (targetUser.hasPlayedBefore()) {
              if (targetUser.hasBirthday()) {
                Birthday birthday = targetUser.getBirthday();
                birthday.setAge(birthday.getAge() + 1);
                targetUser.setBirthday(birthday);
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                  User targets = new User(online);
                  if (online != player.getPlayer()) {
                    if (targets.hasNotifications()) {
                      targets.sendTitle(messages.birthdayTitle(player, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(player, targetUser.getBirthday().getAge()));
                      if (config.enableSong())
                        targets.playSong(config.getSong());
                    }
                    continue;
                  }
                  targetUser.sendTitle(messages.birthdayTitle(player, targetUser.getBirthday().getAge()), messages.birthdaySubtitle(player, targetUser.getBirthday().getAge()));
                  if (config.enableSong())
                    targetUser.playSong(config.getSong());
                }
                if (config.enableFireWorks())
                  targetUser.spawnFireworks(config.fireworkAmount());
                targetUser.setCelebrated(true);
                console.send(messages.prefix() + "&aBirthday of " + name + " celebrated!");
              } else {
                console.send(messages.prefix() + messages.targetNotSet(player));
              }
            } else {
              console.send(messages.prefix() + messages.unknownPlayer(name));
            }
          } else {
            sendHelpMessage("celebrate");
          }
          break;
        case "reload":
          if (Config.manager.reload()) {
            console.send("Reloaded config.yml", Level.OK);
          } else {
            console.send("Couldn't reload config.yml", Level.GRAVE);
          }
          if (Messages.manager.reload()) {
            console.send("Reloaded messages.yml", Level.OK);
          } else {
            console.send("Couldn't reload messages.yml", Level.GRAVE);
          }
          break;
        default:
          sendInvalidArgsMessage();
          break;
      }
    } 
    return false;
  }
  
  private void sendInvalidArgsMessage() {
    console.send("&0&m---------------");
    console.send(" ");
    console.send("{0} &aversion {1}", new Object[] { name, version });
    console.send("&7Type &f/bths help &7 for help");
    console.send(" ");
    console.send("&0&m---------------");
  }
  
  private void sendInvalidArgsMessage(Player player) {
    User user = new User((OfflinePlayer)player);
    user.send("&0&m---------------");
    user.send(" ");
    user.send("{0} &aversion {1}", new Object[] { name, version });
    user.send("&7Type &f/bths help&7 for help");
    user.send(" ");
    user.send("&0&m---------------");
  }
  
  private void sendHelpMessage(String sub) {
    List<String> help;
    switch (sub.toLowerCase()) {
      case "help":
        help = new ArrayList<>();
        help.add("&3&m--------------------");
        help.add(" ");
        help.add("&7/bths help <cmd> &f- &7Displays command help");
        help.add("&7/bths dump <player> &f- &7Removes user data");
        help.add("&7/bths info <player> &f- &7Displays user info");
        help.add("&7/bths celebrate <player> &f- &7Celebrates the birthday");
        help.add("&7/bths migrate &f- &7Migrates data from MySQL");
        help.add(" ");
        help.add("&3&m--------------------");
        console.send(help.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(",", "&r\n&r"));
        return;
      case "dump":
        console.send(messages.prefix() + "&7/bths dump <player> &f- &7Removes player birthday data, including age and notification configuration");
        return;
      case "info":
        console.send(messages.prefix() + "&7/bths info <player> &f- &7Shows player birthday info, age, and notification configuration");
        return;
      case "migrate":
        console.send(messages.prefix() + "&7/bths migrate &f- &7Migrates all players data from MySQL to Yaml");
        return;
      case "celebrate":
        console.send(messages.prefix() + "&7/bths celebrate <player> &f- &7Forces the plugin to celebrate the specified player birthday");
        return;
    } 
    sendInvalidArgsMessage();
  }
  
  private void sendHelpMessage(Player player, String sub) {
    List<String> help;
    User user = new User((OfflinePlayer)player);
    switch (sub.toLowerCase()) {
      case "help":
        help = new ArrayList<>();
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
        return;
      case "dump":
        user.send(messages.prefix() + "&7/bth dump <player> &f- &7Removes player birthday data, including age and notification configuration");
        return;
      case "info":
        user.send(messages.prefix() + "&7/bth info <player> &f- &7Shows player birthday info, age, and notification configuration");
        return;
      case "migrate":
        user.send(messages.prefix() + "&7/bth migrate &f- &7Migrates all players data from MySQL to Yaml");
        return;
      case "celebrate":
        user.send(messages.prefix() + "&7/bths celebrate <player> &f- &7Forces the plugin to celebrate the specified player birthday");
        return;
    } 
    sendInvalidArgsMessage(player);
  }
}
