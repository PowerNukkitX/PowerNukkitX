package cn.nukkit.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads plugin classes from a specific JAR file for a single plugin instance.
 * Handles class caching and delegates global class lookup to JavaPluginLoader.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class PluginClassLoader extends URLClassLoader {

    /** Reference to the JavaPluginLoader that manages this class loader. */
    private final JavaPluginLoader loader;

    /** Local cache of loaded classes for this plugin. */
    private final Map<String, Class<?>> classes = new HashMap<>();

    /**
     * Constructs a new PluginClassLoader for a plugin JAR file.
     *
     * @param loader the JavaPluginLoader managing this loader
     * @param parent the parent class loader
     * @param file   the plugin JAR file
     * @throws MalformedURLException if the file URL is invalid
     */
    public PluginClassLoader(JavaPluginLoader loader, ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.loader = loader;
    }

    /**
     * Finds and loads the class with the specified name, using global lookup.
     *
     * @param name the class name
     * @return the resulting Class object
     * @throws ClassNotFoundException if the class could not be found
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return this.findClass(name, true);
    }

    /**
     * Finds and loads the class with the specified name.
     * Optionally checks the global class cache in JavaPluginLoader.
     *
     * @param name        the class name
     * @param checkGlobal whether to check the global cache
     * @return the resulting Class object
     * @throws ClassNotFoundException if the class could not be found
     */
    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);
                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            if (result != null) {
                classes.put(name, result);
            }
        }

        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }

    /**
     * Returns the set of class names loaded by this class loader.
     *
     * @return set of loaded class names
     */
    Set<String> getClasses() {
        return classes.keySet();
    }
}
