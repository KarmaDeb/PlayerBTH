package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.api.common.ResourceDownloader;
import ml.karmaconfigs.api.common.karma.loader.KarmaBootstrap;
import ml.karmaconfigs.playerbth.utils.PBTHPlugin;

import java.io.File;

public class MainBootstrap implements KarmaBootstrap {

    private final static PBTHPlugin plugin = new PBTHPlugin();

    private final Main loader;

    public MainBootstrap(final Main plugin) {
        loader = plugin;
    }

    @Override
    public void enable() {
        File dependency = new File(loader.getDataFolder() + File.separator + "libraries", "JodaTime.jar");
        ResourceDownloader downloader = new ResourceDownloader(dependency);
        if (!downloader.isDownloaded("https://github.com/JodaOrg/joda-time/releases/download/v2.10.10/joda-time-2.10.10.jar"))
            downloader.download("https://github.com/JodaOrg/joda-time/releases/download/v2.10.10/joda-time-2.10.10.jar");

        getAppender().addJarToClasspath(dependency);
        plugin.initialize();
    }

    @Override
    public void disable() {
        plugin.stop();
        getAppender().close();
    }
}
