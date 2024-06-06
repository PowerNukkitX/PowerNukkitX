package cn.nukkit.lang;

import cn.nukkit.plugin.PluginBase;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 注册插件多语言，要求插件资源文件中存在一个language文件夹，或者指定language文件夹的外部保存路径
 * <p>
 * 多语言文件要求以{@link LangCode}.lang的格式保存
 * <p>
 * To register a plugin for multiple languages, require the existence of a language folder in the plugin resource file, or specify an external path to the language folder
 * <p>
 * Multi-language files are required to be saved in the format {@link LangCode}.lang
 * <p>
 * Only support Java Plugin {@link PluginBase}
 */


@Slf4j
public final class PluginI18nManager {
    private static final HashMap<String, PluginI18n> PLUGINS_MULTI_LANGUAGE = new HashMap<>();

    private PluginI18nManager() {
    }

    /**
     * 重新加载指定插件的多语言，多语言保存在插件jar中的language文件夹下
     * <p>
     * Reload the multilanguage of the specified plugin, which is stored in the language folder of the plugin jar
     *
     * @param plugin the plugin
     * @return the boolean
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
                    // 开始读取文件内容
                    InputStream inputStream = plugin.getResource(name);
                    assert inputStream != null;
                    i18n.reloadLang(LangCode.from(name.substring(9, name.indexOf("."))), inputStream);
                    count++;
                    inputStream.close();
                }
            }
            return count > 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重新加载指定插件的多语言
     * <p>
     * Reload multilingual for a given plugin
     *
     * @param plugin the plugin
     * @param path   language文件夹的路径
     * @return the boolean
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
                    throw new RuntimeException(e);
                }
            }
            return count > 0;
        } else {
            log.error("The path does not represent a folder!");
            return false;
        }
    }

    /**
     * 注册插件多语言
     * <p>
     * Register Plugin Multilanguage
     *
     * @param plugin the plugin
     * @return the boolean
     */
    public static PluginI18n register(PluginBase plugin) {
        try (JarFile jarFile = new JarFile(plugin.getFile())) {
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            var pluginMultiLanguage = new PluginI18n(plugin);
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith("language") && name.endsWith(".json")) {
                    // 开始读取文件内容
                    InputStream inputStream = plugin.getResource(name);
                    assert inputStream != null;
                    pluginMultiLanguage.addLang(LangCode.from(name.substring(9, name.indexOf("."))), inputStream);
                    inputStream.close();
                }
            }
            PLUGINS_MULTI_LANGUAGE.put(plugin.getFile().getName(), pluginMultiLanguage);
            return pluginMultiLanguage;
        } catch (IOException e) {
            throw new RuntimeException("No language exists in the plugin resources folder");
        }
    }

    /**
     * 注册插件多语言
     * <p>
     * Register Plugin Multilanguage
     *
     * @param plugin the plugin
     * @param path   language文件夹的路径<br>Path to the language folder
     * @return the boolean
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
                    throw new RuntimeException(e);
                }
            }
            PLUGINS_MULTI_LANGUAGE.put(plugin.getFile().getName(), pluginMultiLanguage);
            return pluginMultiLanguage;
        } else {
            throw new RuntimeException("The path does not represent a folder or not exists!");
        }
    }

    @Nullable
    public static PluginI18n getI18n(PluginBase plugin) {
        return PLUGINS_MULTI_LANGUAGE.get(plugin.getFile().getName());
    }
}
