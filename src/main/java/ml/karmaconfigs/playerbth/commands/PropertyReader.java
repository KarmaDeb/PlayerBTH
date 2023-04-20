package ml.karmaconfigs.playerbth.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;
import ml.karmaconfigs.playerbth.PlayerBTH;
import org.json.JSONObject;

final class PropertyReader implements PlayerBTH {
  public static Object getProperty(String path) {
    Object value = "";
    try {
      Properties prop = new Properties();
      prop.load(new FileInputStream(new File(plugin.getServer().getWorldContainer(), "server.properties")));
      value = prop.getProperty(path);
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return value;
  }
  
  public static UUID getUUID(String player) {
    try {
      URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + player);
      InputStream in = url.openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return fixUUID(json.getString("id"));
    } catch (Throwable e) {
      e.printStackTrace();
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    } 
  }
  
  private static UUID fixUUID(String id) throws IllegalArgumentException {
    if (id == null)
      throw new IllegalArgumentException(); 
    if (!id.contains("-")) {
      StringBuilder builder = new StringBuilder(id.trim());
      try {
        builder.insert(20, "-");
        builder.insert(16, "-");
        builder.insert(12, "-");
        builder.insert(8, "-");
      } catch (StringIndexOutOfBoundsException e) {
        throw new IllegalArgumentException();
      } 
      return UUID.fromString(builder.toString());
    } 
    return UUID.fromString(id);
  }
  
  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1)
      sb.append((char)cp); 
    return sb.toString();
  }
}
