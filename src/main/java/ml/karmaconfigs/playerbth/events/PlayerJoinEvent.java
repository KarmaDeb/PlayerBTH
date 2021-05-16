package ml.karmaconfigs.playerbth.events;

import ml.karmaconfigs.playerbth.api.BirthdayCelebrateEvent;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.mysql.Migration;
import ml.karmaconfigs.playerbth.utils.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

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

public class PlayerJoinEvent implements Listener, PlayerBTH, Files {

    private final static HashSet<Player> waiting_celebration = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = new User(player);

        Migration migration = new Migration();
        migration.migrateFromYamlToMysql(player);

        if (user.hasBirthday()) {
            if (!waiting_celebration.contains(player)) {
                waiting_celebration.add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            Birthday birthday = user.getBirthday();

                            Date now = new Date();
                            SimpleDateFormat today = new SimpleDateFormat("dd-MM");

                            int day = Integer.parseInt(today.format(now).split("-")[0]);
                            int month = Integer.parseInt(today.format(now).split("-")[1]);

                            if (day == birthday.getDay() && month == birthday.getMonth()) {
                                if (!user.isCelebrated()) {
                                    BirthdayCelebrateEvent event = new BirthdayCelebrateEvent(player);

                                    plugin.getServer().getPluginManager().callEvent(event);

                                    if (!event.isCancelled()) {
                                        Player player = e.getPlayer();

                                        for (Player online : plugin.getServer().getOnlinePlayers()) {
                                            User user = new User(online);

                                            if (online != player) {
                                                if (user.hasNotifications()) {
                                                    user.sendTitle(messages.birthdayTitle(player, user.getBirthday().getAge()), messages.birthdaySubtitle(player, user.getBirthday().getAge()));
                                                    user.playSong(config.getSong());
                                                }
                                            } else {
                                                user.sendTitle(messages.birthdayTitle(player, user.getBirthday().getAge()), messages.birthdaySubtitle(player, user.getBirthday().getAge()));
                                                user.playSong(config.getSong());
                                            }
                                        }

                                        birthday.setAge(birthday.getAge() + 1);
                                        user.setBirthday(birthday);

                                        if (config.enableFireWorks()) {
                                            user.spawnFireworks(config.fireworkAmount());
                                        }

                                        if (config.enableSong()) {
                                            user.playSong(config.getSong());
                                        }

                                        if (config.giveCake()) {
                                            Location location = player.getLocation();
                                            Material type = location.getBlock().getType();
                                            location.getBlock().setType(Material.CAKE);

                                            plugin.getServer().getScheduler().runTaskLater(plugin, () -> location.getBlock().setType(type), 20 * 5);
                                        }

                                        user.setCelebrated(false);
                                    }
                                }
                            }
                        }
                        waiting_celebration.remove(player);
                    }
                }.runTaskLater(plugin, 20 * 30);
            }
        }
    }
}
