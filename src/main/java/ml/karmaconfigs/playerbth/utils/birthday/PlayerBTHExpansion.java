package ml.karmaconfigs.playerbth.utils.birthday;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerBTHExpansion extends PlaceholderExpansion implements Files {

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "birthday";
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public @NotNull String getAuthor() {
        return "KarmaDev";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){
        if (player.getPlayer() != null) {
            User user = new User(player);

            switch (identifier.toLowerCase()) {
                case "set":
                    return String.valueOf(user.hasBirthday()).replace("true", config.birthdaySet()).replace("false", config.birthdayNotSet());
                case "timeleft":
                    if (user.hasBirthday()) {
                        return user.getBirthday().getTimeLeft().split(" ")[1];
                    } else {
                        return config.birthdayNotSet();
                    }
                case "daysleft":
                    if (user.hasBirthday()) {
                        return user.getBirthday().getTimeLeft().split(" ")[0];
                    } else {
                        return config.birthdayNotSet();
                    }
                case "date":
                    if (user.hasBirthday()) {
                        return user.getBirthday().getDay() + "/" + user.getBirthday().getMonth();
                    } else {
                        return config.birthdayNotSet();
                    }
                case "day":
                    if (user.hasBirthday()) {
                        return String.valueOf(user.getBirthday().getDay());
                    } else {
                        return config.birthdayNotSet();
                    }
                case "dayname":
                    if (user.hasBirthday()) {
                        return user.getBirthday().dayName(player.getPlayer().getLocale()).substring(0, 1).toUpperCase() + user.getBirthday().dayName(player.getPlayer().getLocale()).substring(1).toLowerCase();
                    } else {
                        return config.birthdayNotSet();
                    }
                case "month":
                    if (user.hasBirthday()) {
                        return String.valueOf(user.getBirthday().getMonth());
                    } else {
                        return config.birthdayNotSet();
                    }
                case "monthname":
                    if (user.hasBirthday()) {
                        return user.getBirthday().monthName(player.getPlayer().getLocale()).substring(0, 1).toUpperCase() + user.getBirthday().monthName(player.getPlayer().getLocale()).substring(1).toLowerCase();
                    } else {
                        return config.birthdayNotSet();
                    }
                case "age":
                    if (user.hasBirthday()) {
                        return String.valueOf(user.getBirthday().getAge());
                    } else {
                        return config.birthdayNotSet();
                    }
                default:
                    return "Unknown: " + identifier;
            }
        }

        return "player not online";
    }
}
