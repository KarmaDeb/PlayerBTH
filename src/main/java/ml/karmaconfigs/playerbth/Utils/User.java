package ml.karmaconfigs.playerbth.Utils;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.Utils.Birthday.Birthday;
import ml.karmaconfigs.playerbth.Utils.Birthday.Month;
import ml.karmaconfigs.playerbth.Utils.Files.Files;
import ml.karmaconfigs.playerbth.Utils.Files.YamlCreator;
import ml.karmaconfigs.playerbth.Utils.Files.YamlManager;
import ml.karmaconfigs.playerbth.Utils.MySQL.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("unused")
public final class User implements PlayerBTH, Files {

    private final static ArrayList<UUID> celebrated = new ArrayList<>();

    private final DataSys sys = config.getDataSystem();

    private final OfflinePlayer player;
    private Utils utils;

    /**
     * Initialize the user class
     * with the specified player
     *
     * @param player the player
     */
    public User(OfflinePlayer player) {
        this.player = player;
        if (config.getDataSystem().equals(DataSys.MYSQL)) {
            utils = new Utils(player);
            if (!utils.userExists()) {
                utils.createUser();
            }
        }
    }

    /**
     * Send a message to the player
     *
     * @param message the message
     */
    public final void send(String message) {
        if (player.getPlayer() != null && player.getPlayer().isOnline()) {
            player.getPlayer().sendMessage(StringUtils.toColor(message));
        }
    }

    /**
     * Send a message to the player
     * with replaces in it
     *
     * @param message the message
     * @param replaces the replaces
     */
    public final void send(String message, Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            message = message.replace("{" + i + "}", replaces[i].toString());
        }

        send(message);
    }

    /**
     * Send a title to the player
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    public final void sendTitle(String title, String subtitle) {
        if (player.getPlayer() != null && player.getPlayer().isOnline()) {
            Title.sendTitle(player.getPlayer(), title, subtitle);
        }
    }

    /**
     * Set if the player wants to listen
     * to other players birthdays
     *
     * @param value true/false
     */
    public final void setNotifications(boolean value) {
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                file.write("BDNotify", value);
                break;
            case MYSQL:
                utils.setNotifications(value);
                break;
        }
    }

    /**
     * Save the player birthday
     *
     * @param birthday the birthday
     */
    public final void setBirthday(Birthday birthday) {
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                file.write("BDMonth", birthday.getMonth());
                file.write("BDDay", birthday.getDay());
                file.write("BDAge", birthday.getAge());
                break;
            case MYSQL:
                utils.setBirthday(birthday);
                break;
        }
    }

    /**
     * Set if the player wants to listen
     * to other players birthdays
     *
     * @param value true/false
     */
    public final void setNotificationsFile(boolean value) {
        PlayerFile file = new PlayerFile(player);

        file.write("BDNotify", value);
    }

    /**
     * Save the player birthday
     *
     * @param birthday the birthday
     */
    public final void setBirthdayFile(Birthday birthday) {
        PlayerFile file = new PlayerFile(player);

        file.write("BDMonth", birthday.getMonth());
        file.write("BDDay", birthday.getDay());
        file.write("BDAge", birthday.getAge());
    }

    /**
     * Play a song to the player
     *
     * @param name the song name
     */
    public final void playSong(String name) {
        if (player.getPlayer() != null && player.getPlayer().isOnline()) {
            if (PlayerBTH.hasNoteBlock()) {
                File songFile = new File(plugin.getDataFolder() + "/songs", name + ".nbs");
                if (songFile.exists()) {
                    Song song = NBSDecoder.parse(songFile);
                    if (song != null) {
                        if (NoteBlockAPI.isReceivingSong(player.getPlayer())) {
                            NoteBlockAPI.stopPlaying(player.getPlayer());
                        }
                        if (NoteBlockAPI.getSongPlayersByPlayer(player.getPlayer()) != null) {
                            for (SongPlayer radio : NoteBlockAPI.getSongPlayersByPlayer(player.getPlayer())) {
                                radio.removePlayer(player.getPlayer());
                            }
                        }

                        RadioSongPlayer radio = new RadioSongPlayer(song);

                        radio.setRepeatMode(RepeatMode.NO);
                        radio.addPlayer(player.getPlayer());
                        radio.setPlaying(true);
                    }
                }
            }
        }
    }

    /**
     * Set the player birthday as
     * celebrated
     */
    public final void setCelebrated(boolean force) {
        if (force) {
            celebrated.remove(player.getUniqueId());
        }
        if (!celebrated.contains(player.getUniqueId())) {
            YamlCreator creator = new YamlCreator("commands.yml", true);
            creator.createFile();
            creator.setDefaults();
            creator.saveFile();
            YamlManager commands = new YamlManager("commands.yml");

            List<String> runByOthers = new ArrayList<>();
            List<String> runByPlayer = new ArrayList<>();

            for(String str : commands.getList("player")) {
                if (!str.split(" ")[0].toLowerCase().equals("[player]")) {
                    runByOthers.add("/" + str.replace("{player}", Objects.requireNonNull(player.getName())));
                } else {
                    runByPlayer.add("/" + str.replace(str.split(" ")[0] + " ", "").replace("{player}", Objects.requireNonNull(player.getName())));
                }
            }
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                new User(online).executeCommands(runByOthers);
            }
            executeCommands(runByPlayer);
            for (String str : commands.getList("console")) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), str.replace("{player}", Objects.requireNonNull(player.getName())));
            }
            for (String str : commands.getList("message")) {
                send(str.replace("{player}", Objects.requireNonNull(player.getName())));
            }

            celebrated.add(player.getUniqueId());

            long timeLeft = 25 - Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> celebrated.remove(player.getUniqueId()), 20 * timeLeft);
        }
    }

    /**
     * Execute the specified commands
     *
     * @param commands the commands
     */
    private void executeCommands(List<String> commands) {
        if (player.getPlayer() != null) {
            for (String str : commands) {
                player.getPlayer().performCommand(str);
            }
        }
    }

    /**
     * Spawn fireworks at player location
     *
     * @param amount The amount of fireworks
     */
    public final void spawnFireworks(int amount) {
        if (player.getPlayer() != null) {
            if (amount == 0) amount = 1;
            int finalAmount = amount;
            new BukkitRunnable() {
                int back = finalAmount;
                @Override
                public void run() {
                    if (back == 0) {
                        cancel();
                    } else {
                        Firework firework = Objects.requireNonNull(player.getPlayer().getLocation().getWorld()).spawn(player.getPlayer().getLocation(), Firework.class);
                        FireworkMeta fwMeta = firework.getFireworkMeta();

                        fwMeta.addEffect(FireworkEffect.builder().withColor(randomColor()).withTrail().withFlicker().withFade(randomColor()).with(randomEffect()).build());
                        fwMeta.setPower(Files.config.fireworkPower());

                        firework.setFireworkMeta(fwMeta);
                        back--;
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }

    /**
     * Get a random firework effect
     *
     * @return a firework effect
     */
    private FireworkEffect.Type randomEffect() {
        HashMap<Integer, FireworkEffect.Type> types = new HashMap<>();
        for (int i = 0; i < FireworkEffect.Type.values().length; i++) {
            types.put(i, FireworkEffect.Type.values()[i]);
        }
        int random = new Random().nextInt(types.size());
        return types.get(random);
    }

    /**
     * Get a random firework effect
     *
     * @return a firework effect
     */
    private Color[] randomColor() {
        HashMap<Integer, Color> types = new HashMap<>();
        types.put(0, Color.YELLOW);
        types.put(2, Color.WHITE);
        types.put(3, Color.TEAL);
        types.put(4, Color.SILVER);
        types.put(5, Color.PURPLE);
        types.put(6, Color.ORANGE);
        types.put(7, Color.OLIVE);
        types.put(8, Color.NAVY);
        types.put(9, Color.MAROON);
        types.put(10, Color.LIME);
        types.put(11, Color.GREEN);
        types.put(12, Color.GRAY);
        types.put(13, Color.FUCHSIA);
        types.put(14, Color.BLUE);
        types.put(15, Color.BLACK);
        types.put(16, Color.AQUA);
        types.put(17, Color.RED);

        Color[] colors = new Color[3];
        for (int i = 0; i < 3; i++) {
            int random = new Random().nextInt(17);
            if (types.get(random) != null) {
                colors[i] = types.get(random);
            } else {
                i--;
            }
        }

        return colors;
    }

    /**
     * Check if the player has a
     * birthday set
     *
     * @return a boolean
     */
    @SuppressWarnings("all")
    public final boolean hasBirthday() {
        boolean has = false;
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                Object month = file.getVale("BDMonth", null);
                Object day = file.getVale("BDDay", null);
                Object age = file.getVale("BDAge", null);
                if (month != null && day != null && age != null) {
                    has = true;
                } else {
                    try {
                        int Month = Integer.parseInt(month.toString());
                        int Day = Integer.parseInt(day.toString());
                        int Age = Integer.parseInt(age.toString());

                        has = true;
                    } catch (Throwable e) {
                        has = false;
                    }
                }
                break;
            case MYSQL:
                has = utils.hasBirthday();
                break;
        }

        return has;
    }

    /**
     * Check if the player wants to listen
     * other players birthdays
     *
     * @return a boolean
     */
    public final boolean hasNotifications() {
        boolean has = false;
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                try {
                    has = Boolean.parseBoolean(file.getVale("BDNotify", true).toString());
                } catch (Throwable e) {
                    file.write("BDNotify", true);
                    has = true;
                }
                break;
            case MYSQL:
                has = utils.hasNotifications();
                break;
        }
        return has;
    }

    /**
     * Check if the player has a
     * birthday set
     *
     * @return a boolean
     */
    @SuppressWarnings("all")
    public final boolean hasBirthdayFile() {
        PlayerFile file = new PlayerFile(player);

        Object month = file.getVale("BDMonth", null);
        Object day = file.getVale("BDDay", null);
        Object age = file.getVale("BDAge", null);

        if (month != null && day != null && age != null) {
            return true;
        } else {
            try {
                int Month = Integer.parseInt(month.toString());
                int Day = Integer.parseInt(day.toString());
                int Age = Integer.parseInt(age.toString());

                return true;
            } catch (Throwable e) {
                return false;
            }
        }
    }

    /**
     * Check if the player wants to listen
     * other players birthdays
     *
     * @return a boolean
     */
    public final boolean hasNotificationsFile() {
        PlayerFile file = new PlayerFile(player);

        try {
            return Boolean.parseBoolean(file.getVale("BDNotify", true).toString());
        } catch (Throwable e) {
            file.write("BDNotify", true);
             return true;
        }
    }

    /**
     * Check if the player has celebrated
     * his birthday
     *
     * @return a boolean
     */
    public final boolean isCelebrated() {
        return celebrated.contains(player.getUniqueId());
    }

    /**
     * Removes player data
     */
    public final void dumpData() {
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                file.destroy();
                break;
            case MYSQL:
                utils.removeUser();
                break;
        }
    }

    /**
     * Get the user birthday
     *
     * @return a birthday
     */
    public final Birthday getBirthday() {
        Birthday birthday;
        switch (sys) {
            case FILE:
                PlayerFile file = new PlayerFile(player);
                int month = Integer.parseInt(file.getVale("BDMonth", null).toString());
                int day = Integer.parseInt(file.getVale("BDDay", null).toString());
                int age = Integer.parseInt(file.getVale("BDAge", null).toString());
                birthday = new Birthday(Month.byID(month), day);
                birthday.setAge(age);
                break;
            case MYSQL:
                birthday = utils.getBirthday();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sys);
        }

        return birthday;
    }

    /**
     * Get the user birthday
     *
     * @return a birthday
     */
    public final Birthday getFileBirthday() {
        PlayerFile file = new PlayerFile(player);

        int month = Integer.parseInt(file.getVale("BDMonth", null).toString());
        int day = Integer.parseInt(file.getVale("BDDay", null).toString());
        int age = Integer.parseInt(file.getVale("BDAge", null).toString());

        Birthday birthday = new Birthday(Month.byID(month), day);
        birthday.setAge(age);

        return birthday;
    }
}

class Title {
    /**
     * Gets a NMS class
     *
     * @param name the class name
     * @return the NMS class
     */
    protected static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the server version
     *
     * @return the version
     */
    protected static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Send a title to player
     *
     * @param Title the title
     * @param Subtitle the title
     */
    static void sendTitle(Player player, String Title, String Subtitle) {
        try {
            Object titleString = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + StringUtils.toColor(Title) + "\"}");
            Object SubtitleString = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + StringUtils.toColor(Subtitle) + "\"}");

            Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object title = titleConstructor.newInstance(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), titleString, 20 * 3, 20 * 5, 20 * 3);
            Object subtitle = titleConstructor.newInstance(Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), SubtitleString, 20 * 3, 20 * 5, 20 * 3);
            Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, title);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, subtitle);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

final class PlayerFile implements PlayerBTH {

    private final File file;

    /**
     * Initialize the player file class
     *
     * @param player the player
     */
    protected PlayerFile(OfflinePlayer player) {
        File usersFolder = new File(plugin.getDataFolder() + "/users");
        if (!usersFolder.exists()) {
            if (usersFolder.mkdirs()) {
                Server.send("Created users data folder", Server.AlertLevel.INFO);
            }
        }

        file = new File(plugin.getDataFolder() + "/users", player.getUniqueId().toString() + ".player");

        String path = file.getPath().replaceAll("\\\\", "/");

        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    Server.send("Created player data file " + path, Server.AlertLevel.INFO);
                } else {
                    Server.send("An unknown error occurred while creating file " + path, Server.AlertLevel.ERROR);
                }
            }
        } catch (Throwable e) {
            Server.send("An internal error occurred while creating file " + path, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        }
    }

    public final void write(String path, Object value) {
        InputStream file = null;
        InputStreamReader fileReader = null;
        BufferedReader reader = null;

        try {
            file = new FileInputStream(this.file);
            fileReader = new InputStreamReader(file, StandardCharsets.UTF_8);
            reader = new BufferedReader(fileReader);

            List<String> sets = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.contains(":")) {
                    if (line.split(":")[0] != null && !line.split(":")[0].isEmpty()) {
                        String linePath = line.split(":")[0];

                        if (linePath.equals(path)) {
                            sets.add(linePath + ": " + value);
                        } else {
                            sets.add(line);
                        }
                    }
                }
            }

            if (!sets.contains(path + ": " + value)) {
                sets.add(path + ": "+ value);
            }

            FileWriter writer = new FileWriter(this.file);
            for (String str : sets) {
                writer.write(str + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            Server.send("An internal error occurred while writing to file " + path, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
                if (reader != null) {
                    if (reader.lines() != null) {
                        reader.lines().sorted().distinct().close();
                        reader.lines().distinct().close();
                        reader.lines().sorted().close();
                        reader.lines().close();
                        reader.close();
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    public final Object getVale(String path, Object deffault) {
        Object value = deffault;

        InputStream file = null;
        InputStreamReader fileReader = null;
        BufferedReader reader = null;

        try {
            file = new FileInputStream(this.file);
            fileReader = new InputStreamReader(file, StandardCharsets.UTF_8);
            reader = new BufferedReader(fileReader);
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.contains(":")) {
                    if (line.split(":")[0] != null && !line.split(":")[0].isEmpty()) {
                        String linePath = line.split(":")[0];

                        if (linePath.equals(path)) {
                            value = line.replace(linePath + ": ", "");
                            break;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            Server.send("An internal error occurred while writing to file " + path, Server.AlertLevel.ERROR);
            Server.send("&c" + e.fillInStackTrace());
            for (StackTraceElement stack : e.getStackTrace()) {
                Server.send("&b                       " + stack);
            }
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
                if (reader != null) {
                    if (reader.lines() != null) {
                        reader.lines().sorted().distinct().close();
                        reader.lines().distinct().close();
                        reader.lines().sorted().close();
                        reader.lines().close();
                        reader.close();
                    }
                }
            } catch (Throwable ignored) {}
        }

        return value;
    }

    public final void destroy() {
        String path = file.getPath().replaceAll("\\\\", "/");

        if (file.delete()) {
            Server.send("Removed user data file " + path, Server.AlertLevel.INFO);
        }
    }
}
