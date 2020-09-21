package ml.karmaconfigs.playerbth.Events;

import ml.karmaconfigs.playerbth.API.BirthdayCelebrateEvent;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.Files.Files;
import ml.karmaconfigs.playerbth.Utils.MySQL.Migration;
import ml.karmaconfigs.playerbth.Utils.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = new User(player);

        new Migration().migrateFromYamlToMysql(player);

        if (user.hasBirthday()) {
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
                                    birthday.setAge(birthday.getAge() + 1);
                                    user.setBirthday(birthday);

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

                                    if (config.enableFireWorks()) {
                                        user.spawnFireworks(config.fireworkAmount());
                                    }

                                    if (config.enableSong()) {
                                        user.playSong(config.getSong());
                                    }

                                    user.setCelebrated(false);
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 20 * 30);
        }
    }
}
