package ml.karmaconfigs.playerbth.Utils.MySQL;

import ml.karmaconfigs.playerbth.Utils.User;
import org.bukkit.OfflinePlayer;

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

public final class Migration {

    public final void migrateFromSQLToYaml() {
        for (OfflinePlayer player : Utils.getPlayers()) {
            User user = new User(player);

            user.setBirthdayFile(user.getBirthday());
            user.setNotificationsFile(user.hasNotifications());
        }
    }

    public final void migrateFromYamlToMysql(OfflinePlayer player) {
        User user = new User(player);

        if (!user.hasBirthday() && user.hasBirthdayFile()) {
            user.setBirthday(user.getFileBirthday());
            user.setNotifications(user.hasNotificationsFile());
        }
    }
}
