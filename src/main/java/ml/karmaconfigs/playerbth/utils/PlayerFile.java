package ml.karmaconfigs.playerbth.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import ml.karmaconfigs.api.common.data.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.playerbth.PlayerBTH;
import org.bukkit.OfflinePlayer;

final class PlayerFile implements PlayerBTH {
  private final File file;
  
  PlayerFile(OfflinePlayer player) {
    File usersFolder = new File(plugin.getDataFolder() + "/users");
    if (!usersFolder.exists() && 
      usersFolder.mkdirs())
      console.send("Created users data folder", Level.INFO); 
    this.file = new File(plugin.getDataFolder() + "/users", player.getUniqueId() + ".player");
    try {
      if (!this.file.exists())
        if (this.file.createNewFile()) {
          console.send("Created player data file {0}", Level.INFO, FileUtilities.getPrettyFile(this.file));
        } else {
          console.send("An unknown error occurred while creating file {0}", Level.GRAVE, FileUtilities.getPrettyFile(this.file));
        }  
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "Failed to initialize player file");
    } 
  }
  
  PlayerFile(File playerFile) {
    this.file = playerFile;
  }
  
  void write(String path, Object value) {
    InputStream file = null;
    InputStreamReader fileReader = null;
    BufferedReader reader = null;
    try {
      file = Files.newInputStream(this.file.toPath());
      fileReader = new InputStreamReader(file, StandardCharsets.UTF_8);
      reader = new BufferedReader(fileReader);
      List<String> sets = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(":") && 
          line.split(":")[0] != null && !line.split(":")[0].isEmpty()) {
          String linePath = line.split(":")[0];
          if (linePath.equals(path)) {
            sets.add(linePath + ": " + value);
            continue;
          } 
          sets.add(line);
        } 
      } 
      if (!sets.contains(path + ": " + value))
        sets.add(path + ": " + value); 
      FileWriter writer = new FileWriter(this.file);
      for (String str : sets)
        writer.write(str + "\n"); 
      writer.flush();
      writer.close();
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "An error occurred while trying to write to file {0}", FileUtilities.getPrettyFile(this.file));
    } finally {
      try {
        if (file != null)
          file.close(); 
        if (fileReader != null)
          fileReader.close(); 
        if (reader != null && 
          reader.lines() != null) {
          reader.lines().sorted().distinct().close();
          reader.lines().distinct().close();
          reader.lines().sorted().close();
          reader.lines().close();
          reader.close();
        } 
      } catch (Throwable ignored) {}
    } 
  }
  
  Object getVale(String path, Object deffault) {
    Object value = deffault;
    InputStream file = null;
    InputStreamReader fileReader = null;
    BufferedReader reader = null;
    try {
      file = Files.newInputStream(this.file.toPath());
      fileReader = new InputStreamReader(file, StandardCharsets.UTF_8);
      reader = new BufferedReader(fileReader);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(":") && 
          line.split(":")[0] != null && !line.split(":")[0].isEmpty()) {
          String linePath = line.split(":")[0];
          if (linePath.equals(path)) {
            value = line.replace(linePath + ": ", "");
            break;
          } 
        } 
      } 
    } catch (Throwable ex) {
      logger.scheduleLog(Level.GRAVE, ex);
      logger.scheduleLog(Level.INFO, "An error occurred while trying to read file {0}", FileUtilities.getPrettyFile(this.file));
    } finally {
      try {
        if (file != null)
          file.close(); 
        if (fileReader != null)
          fileReader.close(); 
        if (reader != null && 
          reader.lines() != null) {
          reader.lines().sorted().distinct().close();
          reader.lines().distinct().close();
          reader.lines().sorted().close();
          reader.lines().close();
          reader.close();
        } 
      } catch (Throwable ignored) {}
    } 
    return value;
  }
  
  public void destroy() {
    if (this.file.delete())
      console.send("Removed user data file {0}", Level.GRAVE, FileUtilities.getPrettyFile(this.file));
  }
}
