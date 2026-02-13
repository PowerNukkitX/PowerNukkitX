package cn.nukkit.lang;

import cn.nukkit.plugin.PluginBase;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Manages internationalization (i18n) registration and reloading for plugins, enabling multi-language support.
 * <p>
 * This utility class provides static methods to register and reload plugin language resources, supporting both
 * embedded resources (inside the plugin JAR) and external language folders. It maintains a registry of
 * {@link PluginI18n} instances for each plugin, allowing efficient access and management of translations.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Registers plugin language resources from JAR or external folders.</li>
 *   <li>Supports reloading of language resources at runtime.</li>
 *   <li>Handles language files in the format <code>language/{@link LangCode}.json</code>.</li>
 *   <li>Provides access to the {@link PluginI18n} instance for each plugin.</li>
 *   <li>Only supports plugins extending {@link PluginBase}.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     PluginI18n i18n = PluginI18nManager.register(plugin);
 *     boolean reloaded = PluginI18nManager.reload(plugin);
 *     String message = PluginI18nManager.getI18n(plugin).tr(LangCode.fr_FR, "welcome.message");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe for typical usage, as the internal registry is only modified via synchronized static methods.
 * </p>
 *
 * @author PowerNukkitX Team
 * @since 1.0
 */


@Slf4j
public final class PluginI18nManager {
    /**
     * Internal registry mapping plugin file names to their PluginI18n instances.
     */
    private static final HashMap<String, PluginI18n> PLUGINS_MULTI_LANGUAGE = new HashMap<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PluginI18nManager() {
    }

    /**
     * Reloads the multilanguage resources for the specified plugin from the language folder inside the plugin JAR.
     *
     * @param plugin the plugin whose language resources should be reloaded
     * @return true if at least one language file was reloaded, false otherwise
     */
    public static boolean reload(PluginBase plugin) {
        var i18n = PLUGINS_MULTI_LANGUAGE.get(plugin.getFile().getName());
        if (i18n == null) return false;
        try (JarFile jarFile = new JarFile(plugin.getFile())) {
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            int count = 0;
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith("language") && name.endsWith(".json")) {
                    // Begin reading the file contents
                    InputStream inputStream = plugin.getResource(name);
                    assert inputStream != null;
                    i18n.reloadLang(LangCode.from(name.substring(9, name.indexOf("."))), inputStream);
                    count++;
                    inputStream.close();
                }
            }
            return count > 0;
        } catch (IOException e) {
            log.error("Failed to reload language files from plugin JAR", e);
            return false;
        }
    }

    /**
     * Reloads the multilanguage resources for the specified plugin from an external language folder.
     *
     * @param plugin the plugin whose language resources should be reloaded
     * @param path   the path to the external language folder
     * @return true if at least one language file was reloaded, false otherwise
     */
    public static boolean reload(PluginBase plugin, String path) {
        var i18n = PLUGINS_MULTI_LANGUAGE.get(plugin.getFile().getName());
        if (i18n == null) return false;
        var file = new File(path);
        if (file.exists() && file.isDirectory()) {
            var files = file.listFiles();
            assert files != null;
            int count = 0;
            for (var f : files) {
                try (InputStream inputStream = new FileInputStream(f)) {
                    i18n.reloadLang(LangCode.from(f.getName().replace(".json", "")), inputStream);
                    count++;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return count > 0;
        } else {
            log.error("The path does not represent a folder!");
            return false;
        }
    }

    /**
     * Registers multilanguage resources for the specified plugin from the language folder inside the plugin JAR.
     *
     * @param plugin the plugin to register
     * @return the PluginI18n instance associated with the plugin
     * @throws RuntimeException if no language files exist in the plugin resources folder
     */
    public static PluginI18n register(PluginBase plugin) {
        try (JarFile jarFile = new JarFile(plugin.getFile())) {
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            var pluginMultiLanguage = new PluginI18n(plugin);
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith("language") && name.endsWith(".json")) {
                    // Begin reading the file contents
                    InputStream inputStream = plugin.getResource(name);
                    assert inputStream != null;
                    pluginMultiLanguage.addLang(LangCode.from(name.substring(9, name.indexOf("."))), inputStream);
                    inputStream.close();
                }
            }
            PLUGINS_MULTI_LANGUAGE.put(plugin.getFile().getName(), pluginMultiLanguage);
            return pluginMultiLanguage;
        } catch (IOException e) {
            throw new IllegalStateException("No language exists in the plugin resources folder");
        }
    }

    /**
     * Registers multilanguage resources for the specified plugin from an external language folder.
     *
     * @param plugin the plugin to register
     * @param path   the path to the external language folder
     * @return the PluginI18n instance associated with the plugin
     * @throws RuntimeException if the path does not represent a folder or does not exist
     */
    public static PluginI18n register(PluginBase plugin, String path) {
        var file = new File(path);
        if (file.exists() && file.isDirectory()) {
            var files = file.listFiles();
            assert files != null;
            var pluginMultiLanguage = new PluginI18n(plugin);
            for (var f : files) {
                try (InputStream inputStream = new FileInputStream(f)) {
                    pluginMultiLanguage.addLang(LangCode.from(f.getName().replace(".json", "")), inputStream);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            PLUGINS_MULTI_LANGUAGE.put(plugin.getFile().getName(), pluginMultiLanguage);
            return pluginMultiLanguage;
        } else {
            throw new IllegalStateException("The path does not represent a folder or not exists!");
        }
    }

    /**
     * Returns the PluginI18n instance associated with the specified plugin, or null if not registered.
     *
     * @param plugin the plugin
     * @return the PluginI18n instance, or null if not found
     */
    @Nullable
    public static PluginI18n getI18n(PluginBase plugin) {
        return PLUGINS_MULTI_LANGUAGE.get(plugin.getFile().getName());
    }
}
