package ml.karmaconfigs.playerbth.Utils;

import org.bukkit.ChatColor;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public interface StringUtils {

    static String toColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    static String stripColor(String text) {
        return ChatColor.stripColor(toColor(text));
    }
}
