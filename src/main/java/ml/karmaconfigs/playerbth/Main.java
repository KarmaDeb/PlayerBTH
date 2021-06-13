package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.api.common.ResourceDownloader;
import ml.karmaconfigs.api.common.karma.KarmaPlugin;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.loader.KarmaBootstrap;
import ml.karmaconfigs.api.common.karma.loader.SubJarLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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

@KarmaPlugin
public final class Main extends JavaPlugin implements KarmaSource {

    private final KarmaBootstrap plugin;

    public Main() throws Throwable {
        String downloadURL = "https://karmaconfigs.github.io/updates/PlayerBTH/assets/" + getDescription().getVersion() + "/PlayerBTH.jar";

        ResourceDownloader downloader = ResourceDownloader.toCache(
                this,
                "PlayerBTH.injar",
                downloadURL,
                "plugin"
        );
        if (!downloader.isDownloaded(downloadURL))
            downloader.download(downloadURL);

        SubJarLoader loader = new SubJarLoader(getClass().getClassLoader(), downloader.getDestFile());
        plugin = loader.instantiate("ml.karmaconfigs.playerbth.MainBootstrap", Main.class, this);
    }

    @Override
    public void onEnable() {
        plugin.enable();
    }

    @Override
    public void onDisable() {
        plugin.disable();
    }

    @Override
    public String name() {
        return getDescription().getName();
    }

    @Override
    public String version() {
        return getDescription().getVersion();
    }

    @Override
    public String description() {
        return getDescription().getDescription();
    }

    @Override
    public String[] authors() {
        return getDescription().getAuthors().toArray(new String[0]);
    }
}
