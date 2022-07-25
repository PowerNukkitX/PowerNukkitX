package cn.nukkit.plugin;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.js.*;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.PluginException;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CommonJSPlugin implements Plugin, Listener {

    public static final Int2ObjectOpenHashMap<CommonJSPlugin> jsPluginIdMap = new Int2ObjectOpenHashMap<>();
    public static final ConcurrentHashMap<String, JSExternal> jsExternalMap = new ConcurrentHashMap<>();
    public static int globalMaxId = 0;

    protected String pluginName;
    protected File pluginDir;
    protected File mainJSFile;
    protected Server server;

    private boolean isEnabled = false;
    private boolean initialized = false;

    private PluginDescription description;
    private JSPluginLoader jsPluginLoader;
    private PluginLogger logger;

    protected ESMFileSystem fileSystem;
    protected Context jsContext = null;
    protected Value jsExports = null;
    public final LinkedHashMap<String, JSFeature> usedFeatures = new LinkedHashMap<>(0);

    public final int id = globalMaxId++;

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
        for (var each : description.getFeatures()) {
            var feature = JSFeatures.getFeature(each);
            if (feature == null) {
                throw new PluginException("Feature " + each + " requested by " + pluginName + " not found!");
            }
            usedFeatures.put(each, feature);
        }
        var cbd = Context.newBuilder("js")
                .fileSystem(fileSystem = new ESMFileSystem(pluginDir, this))
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLoading(true)
                .allowHostClassLookup(className -> true)
                .allowIO(true)
                .allowExperimentalOptions(true)
                .option("js.esm-eval-returns-exports", "true")
                .option("js.shared-array-buffer", "true")
                .option("js.foreign-object-prototype", "true")
                .option("js.nashorn-compat", "true")
                .option("js.ecmascript-version", "13");
        if (Nukkit.CHROME_DEBUG_PORT != -1 && Nukkit.JS_DEBUG_LIST.contains(description.getName())) {
            cbd.option("inspect", String.valueOf(Nukkit.CHROME_DEBUG_PORT))
                    .option("inspect.Path", description.getName())
                    .option("inspect.Suspend", "true")
                    .option("inspect.Internal", "true")
                    .option("inspect.SourcePath", pluginDir.getAbsolutePath());
        }
        jsContext = cbd.build();
        JSIInitiator.init(jsContext);
        jsContext.getBindings("js").putMember("console", new JSProxyLogger(logger));
        jsPluginIdMap.put(id, this);
        for (var each : usedFeatures.values()) {
            if (each.needsInject()) {
                each.injectIntoContext(jsContext);
            }
        }
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
                    .name("@" + description.getName() + "/" + mainJSFile.getName())
                    .mimeType("application/javascript+module").build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Override
    public void onEnable() {
        var mainFunc = jsExports.getMember("main");
        isEnabled = true;
        try {
            if (mainFunc != null && mainFunc.canExecute()) {
                synchronized (jsContext) {
                    mainFunc.executeVoid();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isEnabled = false;
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Override
    public void onDisable() {
        var closeFunc = jsExports.getMember("close");
        if (closeFunc != null && closeFunc.canExecute()) {
            synchronized (jsContext) {
                closeFunc.executeVoid();
            }
            JSIInitiator.closeContext(jsContext);
            synchronized (jsContext) {
                jsContext.close();
            }
        }
        isEnabled = false;
        jsPluginIdMap.remove(id);
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

    public Context getJsContext() {
        return jsContext;
    }

    public Value getJsExports() {
        return jsExports;
    }

    public ESMFileSystem getFileSystem() {
        return fileSystem;
    }
}
