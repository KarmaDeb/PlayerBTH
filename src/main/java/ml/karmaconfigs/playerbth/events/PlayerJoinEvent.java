package ml.karmaconfigs.playerbth.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.User;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.mysql.Migration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinEvent implements Listener, PlayerBTH, Files {
  private static final HashSet<Player> waiting_celebration = new HashSet<>();
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(final org.bukkit.event.player.PlayerJoinEvent e) {
    final Player player = e.getPlayer();
    final User user = new User(player);
    Migration migration = new Migration();
    migration.migrateFromYamlToMysql(player);
    if (user.hasBirthday() && 
      !waiting_celebration.contains(player)) {
      waiting_celebration.add(player);
      (new BukkitRunnable() {
          public void run() {
            if (player.isOnline()) {
              Birthday birthday = user.getBirthday();
              Date now = new Date();
              SimpleDateFormat today = new SimpleDateFormat("dd-MM");
              int day = Integer.parseInt(today.format(now).split("-")[0]);
              int month = Integer.parseInt(today.format(now).split("-")[1]);
              if (day == birthday.getDay() && month == birthday.getMonth() && 
                !user.isCelebrated()) {
                birthday.setAge(birthday.getAge() + 1);
                user.setBirthday(birthday);
                Player player = e.getPlayer();
                for (Player online : PlayerBTH.plugin.getServer().getOnlinePlayers()) {
                  User onlineUser = new User(online);
                  if (online != player && !user.isPrivate()) {
                    if (onlineUser.hasNotifications()) {
                      onlineUser.sendTitle(Files.messages.birthdayTitle(player, user.getBirthday().getAge()), Files.messages.birthdaySubtitle(player, user.getBirthday().getAge()));
                      onlineUser.playSong(Files.config.getSong());
                    } 
                    continue;
                  }

                  user.sendTitle(Files.messages.birthdayTitle(player, user.getBirthday().getAge()), Files.messages.birthdaySubtitle(player, user.getBirthday().getAge()));
                  user.playSong(Files.config.getSong());
                } 
                if (Files.config.enableFireWorks() && !user.isPrivate())
                  user.spawnFireworks(Files.config.fireworkAmount()); 
                if (Files.config.enableSong())
                  user.playSong(Files.config.getSong()); 
                if (Files.config.giveCake() && !user.isPrivate()) {
                  Location location = player.getLocation();
                  Material type = location.getBlock().getType();
                  location.getBlock().setType(Material.CAKE);
                  PlayerBTH.plugin.getServer().getScheduler().runTaskLater((Plugin)PlayerBTH.plugin, () -> location.getBlock().setType(type), 100L);
                }

                user.setCelebrated(false);
              } 
            } 
            PlayerJoinEvent.waiting_celebration.remove(player);
          }
        }).runTaskLater(plugin, 600L);
    } 
  }
}
