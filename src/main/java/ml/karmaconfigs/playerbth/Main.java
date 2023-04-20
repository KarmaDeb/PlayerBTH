package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.api.bukkit.KarmaPlugin;

public final class Main extends KarmaPlugin {
  private final MainBootstrap plugin;
  
  public Main() {
    super(false);
    this.plugin = new MainBootstrap(this);
  }
  
  public void enable() {
    this.plugin.enable();
  }
  
  public void onDisable() {
    this.plugin.disable();
  }
  
  public String name() {
    return getDescription().getName();
  }
  
  public String version() {
    return getDescription().getVersion();
  }
  
  public String description() {
    return getDescription().getDescription();
  }
  
  public String[] authors() {
    return (String[])getDescription().getAuthors().toArray((Object[])new String[0]);
  }
  
  public String updateURL() {
    return "https://karmadev.es/playerbth.kup";
  }
}
