package ml.karmaconfigs.playerbth.utils;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

import ml.karmaconfigs.api.bukkit.reflection.TitleMessage;
import ml.karmaconfigs.api.common.karma.file.yaml.FileCopy;
import ml.karmaconfigs.api.common.karma.file.yaml.KarmaYamlManager;
import ml.karmaconfigs.api.common.string.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.Main;
import ml.karmaconfigs.playerbth.PlayerBTH;
import ml.karmaconfigs.playerbth.utils.birthday.Birthday;
import ml.karmaconfigs.playerbth.utils.birthday.Month;
import ml.karmaconfigs.playerbth.utils.files.Files;
import ml.karmaconfigs.playerbth.utils.mysql.Utils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class User implements PlayerBTH, Files {
  private static final ArrayList<UUID> celebrated = new ArrayList<>();
  
  private final DataSys sys = config.getDataSystem();
  
  private final OfflinePlayer player;
  
  private Utils utils;
  
  public User(OfflinePlayer player) {
    this.player = player;
    if (config.getDataSystem().equals(DataSys.MYSQL)) {
      this.utils = new Utils(player);
      if (this.utils.notExists())
        this.utils.createUser(); 
    } 
  }
  
  public void send(String message) {
    if (this.player.getPlayer() != null && this.player.getPlayer().isOnline())
      this.player.getPlayer().sendMessage(StringUtils.toColor(message));
  }
  
  public void send(String message, Object... replaces) {
    for (int i = 0; i < replaces.length; i++)
      message = message.replace("{" + i + "}", replaces[i].toString()); 
    send(message);
  }
  
  public void sendTitle(String title, String subtitle) {
    if (this.player.getPlayer() != null && this.player.getPlayer().isOnline()) {
      TitleMessage message = new TitleMessage(this.player.getPlayer(), title, subtitle);
      message.send();
    } 
  }
  
  public void setNotifications(boolean value) {
    PlayerFile file;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        file.write("BDNotify", value);
        break;
      case MYSQL:
        this.utils.setNotifications(value);
        break;
    } 
  }

  public void setPrivate(final boolean value) {
    PlayerFile file;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(player);
        file.write("BDPrivate", value);
        break;
      case MYSQL:
        utils.setPrivate(value);
        break;
    }
  }
  
  public void setBirthday(Birthday birthday) {
    PlayerFile file;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        file.write("BDMonth", birthday.getMonth());
        file.write("BDDay", birthday.getDay());
        file.write("BDAge", birthday.getAge());
        break;
      case MYSQL:
        this.utils.setBirthday(birthday);
        break;
    } 
  }
  
  public void setNotificationsFile(boolean value) {
    PlayerFile file = new PlayerFile(this.player);
    file.write("BDNotify", value);
  }
  
  public void setBirthdayFile(Birthday birthday) {
    PlayerFile file = new PlayerFile(this.player);
    file.write("BDMonth", birthday.getMonth());
    file.write("BDDay", birthday.getDay());
    file.write("BDAge", birthday.getAge());
  }
  
  public void playSong(String name) {
    if (this.player.getPlayer() != null && this.player.getPlayer().isOnline() && 
      PlayerBTH.hasNoteBlock())
      if (!name.equals("Birthday")) {
        File songFile = new File(plugin.getDataFolder() + "/songs", name + ".nbs");
        if (songFile.exists()) {
          Song song = NBSDecoder.parse(songFile);
          if (song != null) {
            if (NoteBlockAPI.isReceivingSong(this.player.getPlayer()))
              NoteBlockAPI.stopPlaying(this.player.getPlayer()); 
            if (NoteBlockAPI.getSongPlayersByPlayer(this.player.getPlayer()) != null)
              for (SongPlayer songPlayer : NoteBlockAPI.getSongPlayersByPlayer(this.player.getPlayer()))
                songPlayer.removePlayer(this.player.getPlayer());  
            RadioSongPlayer radio = new RadioSongPlayer(song);
            radio.setRepeatMode(RepeatMode.NO);
            radio.addPlayer(this.player.getPlayer());
            radio.setPlaying(true);
          } 
        } 
      } else {
        InputStream in_song = Main.class.getResourceAsStream("/Birthday.nbs");
        Song song = NBSDecoder.parse(in_song);
        if (song != null) {
          if (NoteBlockAPI.isReceivingSong(this.player.getPlayer()))
            NoteBlockAPI.stopPlaying(this.player.getPlayer()); 
          if (NoteBlockAPI.getSongPlayersByPlayer(this.player.getPlayer()) != null)
            for (SongPlayer songPlayer : NoteBlockAPI.getSongPlayersByPlayer(this.player.getPlayer()))
              songPlayer.removePlayer(this.player.getPlayer());  
          RadioSongPlayer radio = new RadioSongPlayer(song);
          radio.setRepeatMode(RepeatMode.NO);
          radio.addPlayer(this.player.getPlayer());
          radio.setPlaying(true);
        } 
      }  
  }
  
  public void setCelebrated(boolean force) {
    if (force)
      celebrated.remove(this.player.getUniqueId()); 
    if (!celebrated.contains(this.player.getUniqueId())) {
      PlayerFile pf;
      try {
        File commands_yml = new File(plugin.getDataFolder(), "commands.yml");
        FileCopy creator = new FileCopy(plugin, "commands.yml");
        creator.copy(commands_yml);
      } catch (Throwable ex) {
        logger.scheduleLog(Level.GRAVE, ex);
        logger.scheduleLog(Level.INFO, "Failed to check file commands.yml");
      } 
      KarmaYamlManager commands = new KarmaYamlManager(plugin, "commands");
      List<String> runByOthers = new ArrayList<>();
      List<String> runByPlayer = new ArrayList<>();
      for (String str : commands.getStringList("player")) {
        if (!str.split(" ")[0].equalsIgnoreCase("[player]")) {
          runByOthers.add("/" + str.replace("{player}", Objects.<CharSequence>requireNonNull(this.player.getName())));
          continue;
        } 
        runByPlayer.add("/" + str.replace(str.split(" ")[0] + " ", "").replace("{player}", Objects.<CharSequence>requireNonNull(this.player.getName())));
      } 
      for (Player online : plugin.getServer().getOnlinePlayers())
        (new User((OfflinePlayer)online)).executeCommands(runByOthers); 
      executeCommands(runByPlayer);
      for (String str : commands.getStringList("console"))
        plugin.getServer().dispatchCommand((CommandSender)plugin.getServer().getConsoleSender(), str.replace("{player}", Objects.<CharSequence>requireNonNull(this.player.getName()))); 
      for (String str : commands.getStringList("messages"))
        send(str.replace("{player}", Objects.<CharSequence>requireNonNull(this.player.getName()))); 
      celebrated.add(this.player.getUniqueId());
      String format = DateTime.now().getYear() + "/" + DateTime.now().getMonthOfYear() + "/" + DateTime.now().getDayOfMonth() + " " + DateTime.now().getHourOfDay() + ":" + DateTime.now().getMinuteOfHour() + ":" + DateTime.now().getSecondOfMinute();
      switch (this.sys) {
        case FILE:
          pf = new PlayerFile(this.player);
          pf.write("Celebrated", format);
          break;
        case MYSQL:
          this.utils.setCelebrate(format);
          break;
      } 
    } 
  }
  
  private void executeCommands(List<String> commands) {
    if (this.player.getPlayer() != null)
      for (String str : commands)
        this.player.getPlayer().performCommand(str);  
  }
  
  public void spawnFireworks(int amount) {
    if (this.player.getPlayer() != null) {
      if (amount == 0)
        amount = 1; 
      final int finalAmount = amount;
      (new BukkitRunnable() {
          int back = finalAmount;
          
          public void run() {
            if (this.back == 0) {
              cancel();
            } else {
              Firework firework = (Firework)((World)Objects.<World>requireNonNull(User.this.player.getPlayer().getLocation().getWorld())).spawn(User.this.player.getPlayer().getLocation(), Firework.class);
              FireworkMeta fwMeta = firework.getFireworkMeta();
              fwMeta.addEffect(FireworkEffect.builder().withColor(User.this.randomColor()).withTrail().withFlicker().withFade(User.this.randomColor()).with(User.this.randomEffect()).build());
              fwMeta.setPower(Files.config.fireworkPower());
              firework.setFireworkMeta(fwMeta);
              this.back--;
            } 
          }
        }).runTaskTimer((Plugin)plugin, 0L, 20L);
    } 
  }
  
  private FireworkEffect.Type randomEffect() {
    HashMap<Integer, FireworkEffect.Type> types = new HashMap<>();
    for (int i = 0; i < (FireworkEffect.Type.values()).length; i++)
      types.put(i, FireworkEffect.Type.values()[i]);
    int random = (new Random()).nextInt(types.size());
    return types.get(random);
  }
  
  private Color[] randomColor() {
    List<Color> bukkitColors = new ArrayList<>();
    try {
      Class<Color> colorClass = Color.class;
      Field[] fields = colorClass.getDeclaredFields();
      for (Field f : fields) {
        if (f.getType().equals(Color.class)) {
          bukkitColors.add((Color) f.get(null));
        }
      }

      Color[] colors = new Color[3];
      for (int i = 0; i < 3; i++) {
        int random = Math.max(0, Math.min(new Random().nextInt(bukkitColors.size()), bukkitColors.size() - 1));
        colors[i] = bukkitColors.get(random);
      }

      return colors;
    } catch (Throwable ignored) {}

    return new Color[]{Color.RED, Color.GREEN, Color.BLUE};
  }
  
  public boolean hasBirthday() {
    PlayerFile file;
    Object month, day, age;
    boolean has = false;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        month = file.getVale("BDMonth", null);
        day = file.getVale("BDDay", null);
        age = file.getVale("BDAge", null);

        return month != null && day != null && age != null;
      case MYSQL:
        has = this.utils.hasBirthday();
        break;
    } 
    return has;
  }
  
  public boolean hasNotifications() {
    PlayerFile file;
    boolean has = false;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        try {
          has = Boolean.parseBoolean(file.getVale("BDNotify", true).toString());
        } catch (Throwable e) {
          file.write("BDNotify", true);
          has = true;
        } 
        break;
      case MYSQL:
        has = this.utils.hasNotifications();
        break;
    } 
    return has;
  }

  public boolean isPrivate() {
    PlayerFile file;
    boolean priv = false;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        try {
          priv = Boolean.parseBoolean(file.getVale("BDPrivate", false).toString());
        } catch (Throwable e) {
          file.write("BDPrivate", false);
        }
        break;
      case MYSQL:
        priv = this.utils.isPrivate();
        break;
    }
    return priv;
  }

  public boolean hasBirthdayFile() {
    PlayerFile file = new PlayerFile(this.player);
    Object month = file.getVale("BDMonth", null);
    Object day = file.getVale("BDDay", null);
    Object age = file.getVale("BDAge", null);

    return month != null && day != null && age != null;
  }
  
  public boolean hasNotificationsFile() {
    PlayerFile file = new PlayerFile(this.player);
    try {
      return Boolean.parseBoolean(file.getVale("BDNotify", true).toString());
    } catch (Throwable e) {
      file.write("BDNotify", true);
      return true;
    } 
  }
  
  public boolean isCelebrated() {
    PlayerFile pf;
    String data;
    if (celebrated.contains(this.player.getUniqueId()))
      return true; 
    switch (this.sys) {
      case FILE:
        pf = new PlayerFile(this.player);
        data = pf.getVale("Celebrated", "").toString();
        if (!data.isEmpty()) {
          DateTimeFormatter now = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
          DateTime time = now.parseDateTime(data);
          if (time.plusDays(1).isBeforeNow()) {
            if (time.getDayOfMonth() + 1 == DateTime.now().getDayOfMonth()) {
              String format = DateTime.now().year().get() + "/" + DateTime.now().monthOfYear().get() + "/" + DateTime.now().dayOfMonth().get() + " " + time.getHourOfDay() + ":" + time.getMinuteOfHour() + ":" + time.getSecondOfMinute();
              time = now.parseDateTime(format);
              return time.plusHours(24).isBeforeNow();
            } 
            return true;
          } 
          return false;
        } 
        return false;
      case MYSQL:
        return this.utils.isCelebrated();
    } 
    return false;
  }
  
  public boolean hasPlayedBefore() {
    File player_file = new File(plugin.getDataFolder() + "/users", this.player.getUniqueId() + ".player");
    return player_file.exists();
  }
  
  public void dumpData() {
    PlayerFile file;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        file.destroy();
        break;
      case MYSQL:
        this.utils.removeUser();
        break;
    } 
  }
  
  public Birthday getBirthday() {
    Birthday birthday;
    PlayerFile file;
    int month;
    int day;
    int age;
    switch (this.sys) {
      case FILE:
        file = new PlayerFile(this.player);
        month = Integer.parseInt(file.getVale("BDMonth", null).toString());
        day = Integer.parseInt(file.getVale("BDDay", null).toString());
        age = Integer.parseInt(file.getVale("BDAge", null).toString());
        birthday = new Birthday(Month.byID(month), day);
        birthday.setAge(age);
        return birthday;
      case MYSQL:
        birthday = this.utils.getBirthday();
        return birthday;
    } 
    throw new IllegalStateException("Unexpected value: " + this.sys);
  }
  
  public Birthday getFileBirthday() {
    PlayerFile file = new PlayerFile(this.player);
    int month = Integer.parseInt(file.getVale("BDMonth", null).toString());
    int day = Integer.parseInt(file.getVale("BDDay", null).toString());
    int age = Integer.parseInt(file.getVale("BDAge", null).toString());
    Birthday birthday = new Birthday(Month.byID(month), day);
    birthday.setAge(age);
    return birthday;
  }
}
