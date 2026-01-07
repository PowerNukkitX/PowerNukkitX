package cn.nukkit.lang;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.JSONUtils;
import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Provides internationalization (i18n) support for plugins, enabling multi-language translation and management.
 * <p>
 * This class manages language files for plugins, allowing translation of keys into multiple languages, fallback handling,
 * and dynamic reloading of language resources. It supports loading language data from files or streams, parameterized translations,
 * and integration with the server's global language system as a final fallback.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Translates keys to multiple languages using plugin-specific language files.</li>
 *   <li>Supports parameterized and container-based translations.</li>
 *   <li>Handles fallback language if a translation is missing.</li>
 *   <li>Allows dynamic reloading and addition of language resources at runtime.</li>
 *   <li>Integrates with the server's global language as a final fallback.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     PluginI18n i18n = new PluginI18n(plugin);
 *     i18n.addLang(LangCode.fr_FR, "path/to/fr_FR.json");
 *     String message = i18n.tr(LangCode.fr_FR, "welcome.message", "Player");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If used in a multi-threaded context, external synchronization is required.
 * </p>
 *
 * @author PowerNukkitX Team
 * @since 1.0
 */
@Slf4j
public class PluginI18n {
    /**
     * The fallback language code used when a translation is missing for a specific language.
     */
    private LangCode fallback;

    /**
     * Pattern to extract strings starting with % followed by [a-zA-Z0-9_.-] characters.
     */
    private final Pattern split = Pattern.compile("%[A-Za-z0-9_.-]+");

    /**
     * The plugin instance associated with this i18n manager.
     */
    private final PluginBase plugin;

    /**
     * Stores all loaded language maps for this plugin, indexed by language code.
     */
    private final Map<LangCode, Map<String, String>> MULTI_LANGUAGE;

    /**
     * Constructs a PluginI18n instance for the specified plugin.
     *
     * @param plugin the plugin instance to associate with this i18n manager
     */
    public PluginI18n(PluginBase plugin) {
        this.plugin = plugin;
        this.MULTI_LANGUAGE = new HashMap<>();
        this.fallback = LangCode.en_US;
    }

    /**
     * Translates a text key by looking it up in the language file for the specified language.
     *
     * @param lang the language to translate to
     * @param key  the translation key
     * @return the translated string, or null if not found
     */
    public String tr(LangCode lang, String key) {
        return tr(lang, key, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * Translates a text key with parameters, replacing placeholders in the translation string for the specified language.
     *
     * @param lang the language to translate to
     * @param key  the translation key
     * @param args the parameters to replace in the translation string
     * @return the translated string with parameters applied
     */
    public String tr(LangCode lang, String key, String... args) {
        String baseText = parseLanguageText(lang, key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, String.valueOf(args[i])));
        }
        return baseText;
    }

    /**
     * Translates a text key with object parameters, replacing placeholders in the translation string for the specified language.
     *
     * @param lang the language to translate to
     * @param key  the translation key
     * @param args the parameters to replace in the translation string
     * @return the translated string with parameters applied
     */
    public String tr(LangCode lang, String key, Object... args) {
        String baseText = parseLanguageText(lang, key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, parseArg(args[i])));
        }
        return baseText;
    }

    /**
     * Translates a TextContainer, supporting parameterized translations for the specified language.
     *
     * @param lang the language to translate to
     * @param c    the TextContainer to translate
     * @return the translated string
     */
    public String tr(LangCode lang, TextContainer c) {
        String baseText = this.parseLanguageText(lang, c.getText());
        if (c instanceof TranslationContainer cc) {
            for (int i = 0; i < cc.getParameters().length; i++) {
                baseText = baseText.replace("{%" + i + "}", this.parseLanguageText(lang, cc.getParameters()[i]));
            }
        }
        return baseText;
    }

    /**
     * Gets the multilingual text corresponding to the specified id for the given language, or null if it does not exist.
     * Falls back to the fallback language and then to the server's global language if not found.
     *
     * @param lang the language to use
     * @param id   the translation key
     * @return the translated string, or null if not found
     */
    public String get(LangCode lang, String id) {
        final var map = this.MULTI_LANGUAGE.get(lang);
        final Map<String, String> fallbackMap;
        if (Optional.ofNullable(map).map(t -> t.containsKey(id)).orElse(false)) {
            return map.get(id);
        } else if (Optional.ofNullable(fallbackMap = this.MULTI_LANGUAGE.get(fallback)).map(t -> t.containsKey(id)).orElse(false)) {
            return fallbackMap.get(id);
        } else {
            return Server.getInstance().getLanguage().internalGet(id);
        }
    }

    /**
     * Gets the multilingual text corresponding to the specified id for the given language, or returns the id itself if it does not exist.
     * Falls back to the fallback language and then to the server's global language if not found.
     *
     * @param lang the language to use
     * @param id   the translation key
     * @return the translated string, or the id itself if not found
     */
    public String getOrOriginal(LangCode lang, String id) {
        final var map = this.MULTI_LANGUAGE.get(lang);
        final Map<String, String> fallbackMap;
        if (map.containsKey(id)) {
            return map.get(id);
        } else if ((fallbackMap = this.MULTI_LANGUAGE.get(fallback)).containsKey(id)) {
            return fallbackMap.get(id);
        } else {
            return Server.getInstance().getLanguage().get(id);
        }
    }

    protected String parseLanguageText(LangCode lang, String str) {
        String result = get(lang, str);
        if (result != null) {
            return result;
        } else {
            var matcher = split.matcher(str);
            return matcher.replaceAll(m -> this.getOrOriginal(lang, m.group().substring(1)));
        }
    }

    /**
     * Add lang.
     *
     * @param langName the lang name
     * @param path     the path
     */
    public void addLang(LangCode langName, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            Preconditions.checkArgument(file.getName().endsWith(".json"));
            try (FileInputStream stream = new FileInputStream(file)) {
                this.MULTI_LANGUAGE.put(langName, parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
            }
        } catch (IOException e) {
            log.error("Failed to load language at {}", path, e);
        }
    }

    /**
     * Add lang.
     *
     * @param langName the lang name
     * @param stream   the stream
     */
    public void addLang(LangCode langName, InputStream stream) {
        try {
            this.MULTI_LANGUAGE.put(langName, parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
        } catch (IOException e) {
            log.error("Failed to parse the language input stream", e);
        }
    }

    /**
     * Reload all lang for the i18n.
     *
     * @return whether reload success
     */
    public boolean reloadLangAll() {
        return PluginI18nManager.reload(plugin);
    }

    /**
     * Reload all lang for the i18n from the path folder.
     *
     * @param path the folder
     * @return whether reload success
     */
    public boolean reloadLangAll(String path) {
        return PluginI18nManager.reload(plugin, path);
    }

    /**
     * Reload lang boolean.
     *
     * @param lang the lang
     * @param path the path
     * @return the boolean
     */
    public boolean reloadLang(LangCode lang, String path) {
        try {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            try (FileInputStream stream = new FileInputStream(file)) {
                return reloadLang(lang, new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            log.error("Failed to load language at {}", path, e);
            return false;
        }
    }

    /**
     * Reload lang boolean.
     *
     * @param lang   the lang
     * @param stream the stream
     * @return the boolean
     */
    public boolean reloadLang(LangCode lang, InputStream stream) {
        return reloadLang(lang, new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }

    /**
     * Gets fallback language.
     *
     * @return the fallback language
     */
    public LangCode getFallbackLanguage() {
        return fallback;
    }

    /**
     * Sets fallback language.
     *
     * @param fallback the fallback
     */
    public void setFallbackLanguage(LangCode fallback) {
        this.fallback = fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginI18n that = (PluginI18n) o;
        return Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return plugin.hashCode();
    }

    protected String parseArg(Object arg) {
        switch (arg.getClass().getSimpleName()) {
            case "int[]" -> {
                return Arrays.toString((int[]) arg);
            }
            case "double[]" -> {
                return Arrays.toString((double[]) arg);
            }
            case "float[]" -> {
                return Arrays.toString((float[]) arg);
            }
            case "short[]" -> {
                return Arrays.toString((short[]) arg);
            }
            case "byte[]" -> {
                return Arrays.toString((byte[]) arg);
            }
            case "long[]" -> {
                return Arrays.toString((long[]) arg);
            }
            case "boolean[]" -> {
                return Arrays.toString((boolean[]) arg);
            }
            default -> {
                return String.valueOf(arg);
            }
        }
    }

    private boolean reloadLang(LangCode lang, BufferedReader reader) {
        Map<String, String> d = this.MULTI_LANGUAGE.get(lang);
        Map<String, String> map = JSONUtils.from(reader, new TypeToken<Map<String, String>>() {
        });
        if (d == null) {
            this.MULTI_LANGUAGE.put(lang, map);
        } else {
            d.clear();
            d.putAll(map);
        }
        return true;
    }

    private Map<String, String> parseLang(BufferedReader reader) throws IOException {
        return JSONUtils.from(reader, new TypeToken<Map<String, String>>() {
        });
    }
}
