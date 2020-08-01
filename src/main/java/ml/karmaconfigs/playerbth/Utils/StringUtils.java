package ml.karmaconfigs.playerbth.Utils;

import org.bukkit.ChatColor;

public interface StringUtils {

    static String toColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    static String stripColor(String text) {
        return ChatColor.stripColor(toColor(text));
    }
}
