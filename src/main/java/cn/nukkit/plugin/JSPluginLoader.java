package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

@Log4j2
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class JSPluginLoader implements PluginLoader {

    public final Server server;

    public JSPluginLoader(Server server) {
        this.server = server;
    }

    @Override
    public Plugin loadPlugin(String filename) throws Exception {
        return loadPlugin(new File(filename));
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        if(!file.exists() || !file.isDirectory()) {
            return null;
        }
        var pluginDescription = new PluginDescription(Files.readString(file.toPath().resolve("plugin.yml")));
        log.info(this.server.getLanguage().tr("nukkit.plugin.load", pluginDescription.getFullName()));
        var jsPlugin = new CommonJSPlugin();
        jsPlugin.init(this, file, pluginDescription);
        jsPlugin.onLoad();
        return jsPlugin;
    }

    @Override
    public PluginDescription getPluginDescription(String filename) {
        return getPluginDescription(new File(filename));
    }

    @Override
    public PluginDescription getPluginDescription(File file) {
        try {
            return new PluginDescription(Files.readString(file.toPath().resolve("plugin.yml")));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pattern[] getPluginFilters() {
        return new Pattern[]{Pattern.compile("^@.+$")};
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof CommonJSPlugin jsPlugin && !plugin.isEnabled()) {
            log.info(this.server.getLanguage().tr("nukkit.plugin.enable", plugin.getDescription().getFullName()));
            jsPlugin.onEnable();
            this.server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof CommonJSPlugin jsPlugin && plugin.isEnabled()) {
            log.info(this.server.getLanguage().tr("nukkit.plugin.disable", plugin.getDescription().getFullName()));
            this.server.getServiceManager().cancel(plugin);
            this.server.getPluginManager().callEvent(new PluginDisableEvent(plugin));
            jsPlugin.onDisable();
        }
    }
}
