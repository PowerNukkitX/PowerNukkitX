package cn.nukkit.plugin;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.js.ESMFileSystem;
import cn.nukkit.utils.Config;
import org.graalvm.polyglot.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CommonJSPlugin implements Plugin {

    protected String pluginName;
    protected File pluginDir;
    protected File mainJSFile;
    protected Server server;

    private boolean isEnabled = false;
    private boolean initialized = false;

    private PluginDescription description;
    private JSPluginLoader jsPluginLoader;
    private PluginLogger logger;

    protected Context jsContext = null;
    protected Value jsExports = null;

    public final void init(JSPluginLoader jsPluginLoader, File pluginDir, PluginDescription pluginDescription) {
        this.jsPluginLoader = jsPluginLoader;
        this.server = jsPluginLoader.server;
        this.pluginDir = pluginDir;
        this.mainJSFile = new File(pluginDir, pluginDescription.getMain());
        if (!mainJSFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                mainJSFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.pluginName = pluginDescription.getName();
        this.description = pluginDescription;
        this.logger = new PluginLogger(this);
        var cbd = Context.newBuilder("js")
                .fileSystem(new ESMFileSystem(pluginDir))
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowIO(true)
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", "true")
                .option("js.esm-eval-returns-exports", "true");
        if(Nukkit.CHROME_DEBUG_PORT != -1) {
            cbd.option("inspect", String.valueOf(Nukkit.CHROME_DEBUG_PORT))
                    .option("inspect.Path", description.getName())
                    .option("inspect.Suspend", "false")
                    .option("inspect.Internal", "true")
                    .option("inspect.SourcePath", pluginDir.getAbsolutePath());
        }
        jsContext = cbd.build();
        this.initialized = true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public void onLoad() {
        try {
            jsExports = jsContext.eval(Source.newBuilder("js", mainJSFile)
                    .name(mainJSFile.getName())
                    .mimeType("application/javascript+module").build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        var mainFunc = jsExports.getMember("main");
        if (mainFunc != null && mainFunc.canExecute()) {
            mainFunc.executeVoid();
        }
        isEnabled = true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onDisable() {
        var closeFunc = jsExports.getMember("close");
        if (closeFunc != null && closeFunc.canExecute()) {
            closeFunc.executeVoid();
            jsContext.close();
        }
        isEnabled = false;
    }

    @Override
    public boolean isDisabled() {
        return !isEnabled;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescription getDescription() {
        return description;
    }

    @Override
    public InputStream getResource(String filename) {
        return null;
    }

    @Override
    public boolean saveResource(String filename) {
        return false;
    }

    @Override
    public boolean saveResource(String filename, boolean replace) {
        return false;
    }

    @Override
    public boolean saveResource(String filename, String outputName, boolean replace) {
        return false;
    }

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public PluginLogger getLogger() {
        return logger;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return jsPluginLoader;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
