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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class BaseLang {
    /**
     * 默认备选语言，对应language文件夹
     */
    public static final String FALLBACK_LANGUAGE = "eng";

    /**
     * The Lang name.
     */
    protected final String langName;

    /**
     * 本地语言，从nukkit.yml中指定
     */
    protected Map<String, String> lang;

    /**
     * 备选语言映射，当从本地语言映射中无法翻译时调用备选语言映射，默认为英文
     */
    protected Map<String, String> fallbackLang = new HashMap<>();

    //用于提取字符串中%后带有[a-zA-Z0-9_.-]这些字符的字符串的模式
    private final Pattern split = Pattern.compile("%[A-Za-z0-9_.-]+");


    public BaseLang(String lang) {
        this(lang, null);
    }

    public BaseLang(String lang, String path) {
        this(lang, path, FALLBACK_LANGUAGE);
    }

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
                throw new RuntimeException(e);
            }
        } else {

            this.lang = this.loadLang(Path.of(path).resolve(this.langName + "/lang.json").toString());
            if (useFallback) this.fallbackLang = this.loadLang(path + fallback + "/lang.json");
        }
        if (this.fallbackLang == null) this.fallbackLang = this.lang;
    }

    public Map<String, String> getLangMap() {
        return lang;
    }

    public Map<String, String> getFallbackLangMap() {
        return fallbackLang;
    }

    public String getName() {
        return this.get("language.name");
    }

    public String getLang() {
        return langName;
    }

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

    protected Map<String, String> loadLang(InputStream stream) {
        try {
            return parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("Failed to parse the language input stream", e);
            return null;
        }
    }

    private Map<String, String> parseLang(BufferedReader reader) throws IOException {
        return JSONUtils.from(reader, new TypeToken<Map<String, String>>() {
        });
    }

    /**
     * 翻译一个文本key，key从语言文件中查询
     *
     * @param key the key
     * @return the string
     */
    public String tr(String key) {
        return tr(key, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充结果
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    public String tr(String key, String... args) {
        String baseText = parseLanguageText(key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(String.valueOf(args[i])));
        }
        return baseText;
    }

    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充结果
     * <p>
     * Translate a text key, the key is queried from the language file and the result is populated according to the given parameters
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    public String tr(String key, Object... args) {
        String baseText = parseLanguageText(key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(parseArg(args[i])));
        }
        return baseText;
    }

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
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充结果
     * <p>
     * Translate a text key, the key is queried from the language file and the result is populated according to the given parameters
     *
     * @param str    the str
     * @param params the params
     * @param prefix str的前缀<br>Prefix of str
     * @param mode   为true，则只翻译以指定前缀的多语言文本，为false则只翻译不带有指定前缀的多语言文本<br>If true translate only multilingual text with the specified prefix, false translate only multilingual text without the specified prefix
     * @return the string
     */
    public String tr(String str, String[] params, String prefix, boolean mode) {
        String baseText = parseLanguageText(str, prefix, mode);
        for (int i = 0; i < params.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(parseArg(params[i]), prefix, mode));
        }
        return baseText;
    }

    /**
     * 获取指定id对应的多语言文本，若不存在则返回null
     *
     * @param id the id
     * @return the string
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
     * 获取指定id对应的多语言文本，若不存在则返回id本身
     *
     * @param id the id
     * @return the string
     */
    public String get(String id) {
        if (this.lang.containsKey(id)) {
            return this.lang.get(id);
        } else if (this.fallbackLang.containsKey(id)) {
            return this.fallbackLang.get(id);
        }
        return id;
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

    protected String parseLanguageText(String str) {
        String result = internalGet(str);
        if (result != null) {
            return result;
        } else {
            var matcher = split.matcher(str);
            return matcher.replaceAll(m -> this.get(m.group().substring(1)));
        }
    }

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
