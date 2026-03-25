package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Loads, enables, disables, and manages Java-based plugins for the server.
 * Handles plugin class loading, description parsing, and plugin lifecycle events.
 *
 * @author Nukkit Team
 */
@Slf4j
public class JavaPluginLoader implements PluginLoader {

    /** Reference to the server instance. */
    private final Server server;

    /** Global cache of loaded classes by name. */
    private final Map<String, Class<?>> classes = new HashMap<>();

    /** Map of plugin name to their class loader. */
    @Getter
    protected final Map<String, PluginClassLoader> classLoaders = new HashMap<>();

    /**
     * Constructs a new JavaPluginLoader for the given server.
     *
     * @param server the server instance
     */
    public JavaPluginLoader(Server server) {
        this.server = server;
    }

    /**
     * Loads a plugin from the specified file.
     *
     * @param file the plugin JAR file
     * @return the loaded Plugin instance, or null if not found
     * @throws Exception if loading fails
     */
    @Override
    public Plugin loadPlugin(File file) throws Exception {
        PluginDescription description = this.getPluginDescription(file);
        if (description != null) {
            log.info(this.server.getLanguage().tr("nukkit.plugin.load", description.getFullName()));
            File dataFolder = new File(file.getParentFile(), description.getName());
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw new IllegalStateException("Projected dataFolder '" + dataFolder + "' for " + description.getName() + " exists and is not a directory");
            }

            String className = description.getMain();
            PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(), file);
            this.classLoaders.put(description.getName(), classLoader);
            PluginBase plugin;
            try {
                Class<?> javaClass = classLoader.loadClass(className);

                if (!PluginBase.class.isAssignableFrom(javaClass)) {
                    throw new PluginException("Main class `" + description.getMain() + "' does not extend PluginBase");
                }

                try {
                    Class<? extends PluginBase> pluginClass = javaClass.asSubclass(PluginBase.class);
                    plugin = pluginClass.getDeclaredConstructor().newInstance();
                    this.initPlugin(plugin, classLoader, description, dataFolder, file);
                    return plugin;
                } catch (ClassCastException e) {
                    throw new PluginException("Error whilst initializing main class `" + description.getMain() + "'", e);
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("An exception happened while initializing the plugin {}, {}, {}, {}", file, className, description.getName(), description.getVersion(), e);
                }
            } catch (ClassNotFoundException e) {
                throw new PluginException("Couldn't load plugin " + description.getName() + ": main class not found");
            }
        }
        return null;
    }

    /**
     * Loads a plugin from the specified filename.
     *
     * @param filename the plugin JAR filename
     * @return the loaded Plugin instance, or null if not found
     * @throws Exception if loading fails
     */
    @Override
    public Plugin loadPlugin(String filename) throws Exception {
        return this.loadPlugin(new File(filename));
    }

    /**
     * Reads the plugin description from a JAR file.
     *
     * @param file the plugin JAR file
     * @return the PluginDescription, or null if not found
     */
    @Override
    public PluginDescription getPluginDescription(File file) {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("powernukkitx.yml");
            if (entry == null) {
                entry = jar.getJarEntry("nukkit.yml");
                if (entry == null) {
                    entry = jar.getJarEntry("plugin.yml");
                    if (entry == null) {
                        return null;
                    }
                }
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                return new PluginDescription(Utils.readFile(stream));
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads the plugin description from a JAR filename.
     *
     * @param filename the plugin JAR filename
     * @return the PluginDescription, or null if not found
     */
    @Override
    public PluginDescription getPluginDescription(String filename) {
        return this.getPluginDescription(new File(filename));
    }

    /**
     * Returns the file patterns for plugin JARs.
     *
     * @return array of regex patterns
     */
    @Override
    public Pattern[] getPluginFilters() {
        return new Pattern[]{Pattern.compile("^.+\\.jar$")};
    }

    /**
     * Initializes a plugin instance with its loader, description, and data folder.
     *
     * @param plugin      the plugin instance
     * @param classLoader the class loader
     * @param description the plugin description
     * @param dataFolder  the data folder
     * @param file        the plugin JAR file
     */
    private void initPlugin(PluginBase plugin, ClassLoader classLoader, PluginDescription description, File dataFolder, File file) {
        plugin.init(this, classLoader, this.server, description, dataFolder, file);
        plugin.onLoad();
    }

    /**
     * Enables the specified plugin, firing the enable event.
     *
     * @param plugin the plugin to enable
     */
    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && !plugin.isEnabled()) {
            log.info(this.server.getLanguage().tr("nukkit.plugin.enable", plugin.getDescription().getFullName()));
            ((PluginBase) plugin).setEnabled(true);
            this.server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    /**
     * Disables the specified plugin, firing the disable event.
     *
     * @param plugin the plugin to disable
     */
    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && plugin.isEnabled()) {
            if (plugin == InternalPlugin.INSTANCE) {
                throw new UnsupportedOperationException("The PowerNukkitX Internal Plugin cannot be disabled");
            }
            log.info(this.server.getLanguage().tr("nukkit.plugin.disable", plugin.getDescription().getFullName()));
            this.server.getServiceManager().cancel(plugin);
            this.server.getPluginManager().callEvent(new PluginDisableEvent(plugin));
            ((PluginBase) plugin).setEnabled(false);
        }
    }

    /**
     * Returns a loaded class by name from the global cache or plugin class loaders.
     *
     * @param name the class name
     * @return the Class object, or null if not found
     */
    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);
        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (PluginClassLoader loader : this.classLoaders.values()) {
                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    /**
     * Caches a class globally by name.
     *
     * @param name  the class name
     * @param clazz the Class object
     */
    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    /**
     * Removes a class from the global cache.
     *
     * @param name the class name
     */
    private void removeClass(String name) {
        classes.remove(name);
    }
}
