package ml.karmaconfigs.playerbth.API;

import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
