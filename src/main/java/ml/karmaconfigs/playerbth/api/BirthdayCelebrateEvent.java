package ml.karmaconfigs.playerbth.api;

import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
public class BirthdayCelebrateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private static boolean isCancelled = false;

    private final User user;
    private final Player player;

    /**
     * Initialize the event
     *
     * @param player the player
     */
    public BirthdayCelebrateEvent(Player player) {
        user = new User(player);
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    /**
     * Get the players
     *
     * @return a player
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * Get the user birthday
     *
     * @return a birthday
     */
    public final Birthday getBirthday() {
        return user.getBirthday();
    }

    /**
     * Check if the user has notifications
     *
     * @return a boolean
     */
    public final boolean hasNotifications() {
        return user.hasNotifications();
    }

    @Override
    public final HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
