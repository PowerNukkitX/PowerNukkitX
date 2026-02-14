package cn.nukkit.lang;

import cn.nukkit.utils.JSONUtils;
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
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BaseLang is responsible for managing language translations for the application.
 * <p>
 * It loads language files, provides translation for keys, supports fallback languages,
 * and allows parameterized translations. The class supports loading language data from
 * both resource streams and file paths, and provides several translation methods for
 * different use cases, including parameterized and prefix-based translations.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Loads language mappings from JSON files.</li>
 *   <li>Supports a fallback language if a translation is missing.</li>
 *   <li>Provides translation methods with and without parameters.</li>
 *   <li>Handles translation containers for advanced use cases.</li>
 *   <li>Allows translation filtering by prefix and mode.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 *     BaseLang lang = new BaseLang("eng");
 *     String translated = lang.tr("welcome.message", "Player");
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If used in a multi-threaded context, external synchronization is required.
 * </p>
 *
 * @author MagicDroidX (Nukkit Project)
 * @since 1.0
 */
@Slf4j
public class BaseLang {
    /**
     * The default fallback language code, corresponding to the language folder.
     */
    public static final String FALLBACK_LANGUAGE = "eng";

    /**
     * The language code (e.g., "eng", "fra").
     */
    protected final String langName;

    /**
     * The main language map loaded from the language file, as specified in pnx.yml.
     */
    protected Map<String, String> lang;

    /**
     * The fallback language map, used when a translation is missing from the main language map. Defaults to English.
     */
    protected Map<String, String> fallbackLang = new HashMap<>();

    /**
     * Pattern to extract strings starting with % followed by [a-zA-Z0-9_.-] characters.
     */
    private final Pattern split = Pattern.compile("%[A-Za-z0-9_.-]+");


    /**
     * Constructs a BaseLang instance for the specified language code, using the default language path and fallback language.
     *
     * @param lang the language code (e.g., "eng", "fra")
     */
    public BaseLang(String lang) {
        this(lang, null);
    }

    /**
     * Constructs a BaseLang instance for the specified language code and path, using the default fallback language.
     *
     * @param lang the language code (e.g., "eng", "fra")
     * @param path the path to the language files directory
     */
    public BaseLang(String lang, String path) {
        this(lang, path, FALLBACK_LANGUAGE);
    }

    /**
     * Constructs a BaseLang instance for the specified language code, path, and fallback language.
     * Loads the language and fallback language maps from the specified locations.
     *
     * @param lang     the language code (e.g., "eng", "fra")
     * @param path     the path to the language files directory (if null, uses default resource path)
     * @param fallback the fallback language code
     */
    public BaseLang(String lang, String path, String fallback) {
        this.langName = lang.toLowerCase(Locale.ENGLISH);
        boolean useFallback = !lang.equals(fallback);

        if (path == null) {
            path = "language/";
            try {
                this.lang = this.loadLang(this.getClass().getModule().getResourceAsStream(path + this.langName + "/lang.json"));
                if (useFallback)
                    this.fallbackLang = this.loadLang(this.getClass().getModule().getResourceAsStream(path + fallback + "/lang.json"));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {

            this.lang = this.loadLang(Path.of(path).resolve(this.langName + "/lang.json").toString());
            if (useFallback) this.fallbackLang = this.loadLang(path + fallback + "/lang.json");
        }
        if (this.fallbackLang == null) this.fallbackLang = this.lang;
    }

    /**
     * Returns the main language map.
     *
     * @return the language map for the current language
     */
    public Map<String, String> getLangMap() {
        return lang;
    }

    /**
     * Returns the fallback language map.
     *
     * @return the fallback language map
     */
    public Map<String, String> getFallbackLangMap() {
        return fallbackLang;
    }

    /**
     * Returns the display name of the current language, as defined by the key "language.name".
     *
     * @return the display name of the language
     */
    public String getName() {
        return this.get("language.name");
    }

    /**
     * Returns the language code for this BaseLang instance.
     *
     * @return the language code (e.g., "eng", "fra")
     */
    public String getLang() {
        return langName;
    }

    /**
     * Loads a language map from a file path.
     *
     * @param path the file path to the language JSON file
     * @return the loaded language map, or null if loading fails
     */
    protected Map<String, String> loadLang(String path) {
        try {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            try (FileInputStream stream = new FileInputStream(file)) {
                return parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            }
        } catch (IOException e) {
            log.error("Failed to load language at {}", path, e);
            return null;
        }
    }

    /**
     * Loads a language map from an input stream.
     *
     * @param stream the input stream to the language JSON file
     * @return the loaded language map, or null if loading fails
     */
    protected Map<String, String> loadLang(InputStream stream) {
        try {
            return parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("Failed to parse the language input stream", e);
            return null;
        }
    }

    /**
     * Parses a language map from a buffered reader.
     *
     * @param reader the buffered reader for the language JSON file
     * @return the parsed language map
     * @throws IOException if an I/O error occurs
     */
    private Map<String, String> parseLang(BufferedReader reader) throws IOException {
        return JSONUtils.from(reader, new TypeToken<Map<String, String>>() {
        });
    }

    /**
     * Translates a text key by looking it up in the language file.
     *
     * @param key the translation key
     * @return the translated string, or the key itself if not found
     */
    public String tr(String key) {
        return tr(key, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * Translates a text key with parameters, replacing placeholders in the translation string.
     *
     * @param key  the translation key
     * @param args the parameters to replace in the translation string
     * @return the translated string with parameters applied
     */
    public String tr(String key, String... args) {
        String baseText = parseLanguageText(key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(String.valueOf(args[i])));
        }
        return baseText;
    }

    /**
     * Translates a text key with object parameters, replacing placeholders in the translation string.
     *
     * @param key  the translation key
     * @param args the parameters to replace in the translation string
     * @return the translated string with parameters applied
     */
    public String tr(String key, Object... args) {
        String baseText = parseLanguageText(key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(parseArg(args[i])));
        }
        return baseText;
    }

    /**
     * Translates a TextContainer, supporting parameterized translations.
     *
     * @param c the TextContainer to translate
     * @return the translated string
     */
    public String tr(TextContainer c) {
        String baseText = this.parseLanguageText(c.getText());
        if (c instanceof TranslationContainer cc) {
            for (int i = 0; i < cc.getParameters().length; i++) {
                baseText = baseText.replace("{%" + i + "}", this.parseLanguageText(cc.getParameters()[i]));
            }
        }
        return baseText;
    }

    /**
     * Translates a text key with parameters, prefix, and mode filtering.
     *
     * @param str    the translation key
     * @param params the parameters to replace in the translation string
     * @param prefix the prefix to filter translation keys
     * @param mode   if true, only translate keys with the specified prefix; if false, only translate keys without the prefix
     * @return the translated string with parameters applied
     */
    public String tr(String str, String[] params, String prefix, boolean mode) {
        String baseText = parseLanguageText(str, prefix, mode);
        for (int i = 0; i < params.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(parseArg(params[i]), prefix, mode));
        }
        return baseText;
    }

    /**
     * Returns the translation for the given id from the main or fallback language map, or null if not found.
     *
     * @param id the translation key
     * @return the translated string, or null if not found
     */
    public String internalGet(String id) {
        if (this.lang.containsKey(id)) {
            return this.lang.get(id);
        } else if (this.fallbackLang.containsKey(id)) {
            return this.fallbackLang.get(id);
        }
        return null;
    }

    /**
     * Returns the translation for the given id from the main or fallback language map, or the id itself if not found.
     *
     * @param id the translation key
     * @return the translated string, or the id itself if not found
     */
    public String get(String id) {
        if (this.lang.containsKey(id)) {
            return this.lang.get(id);
        } else if (this.fallbackLang.containsKey(id)) {
            return this.fallbackLang.get(id);
        }
        return id;
    }

    /**
     * Converts an argument to its string representation, handling arrays and objects.
     *
     * @param arg the argument to convert
     * @return the string representation of the argument
     */
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

    /**
     * Parses and translates a string, replacing placeholders with translations if present.
     *
     * @param str the string to parse
     * @return the translated string, or the original if not found
     */
    protected String parseLanguageText(String str) {
        String result = internalGet(str);
        if (result != null) {
            return result;
        } else {
            var matcher = split.matcher(str);
            return matcher.replaceAll(m -> this.get(m.group().substring(1)));
        }
    }

    /**
     * Parses and translates a string with prefix and mode filtering, replacing placeholders with translations if present.
     *
     * @param str    the string to parse
     * @param prefix the prefix to filter translation keys
     * @param mode   if true, only translate keys with the specified prefix; if false, only translate keys without the prefix
     * @return the translated string, or the original if not found
     */
    protected String parseLanguageText(String str, String prefix, boolean mode) {
        if (mode && !str.startsWith(prefix)) {
            return str;
        }
        if (!mode && str.startsWith(prefix)) {
            return str;
        }
        String result = internalGet(str);
        if (result != null) {
            return result;
        } else {
            var matcher = split.matcher(str);
            return matcher.replaceAll(m -> {
                var s = m.group().substring(1);
                if (mode) {
                    if (s.startsWith(prefix)) {
                        return this.get(s);
                    } else return s;
                } else {
                    if (!s.startsWith(prefix)) {
                        return this.get(s);
                    } else return s;
                }
            });
        }
    }
}
