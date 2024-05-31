
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


@Slf4j
public class PluginI18n {
    /**
     * 插件多语言的默认备选语言
     */
    private LangCode fallback;
    private final Pattern $1 = Pattern.compile("%[A-Za-z0-9_.-]+");
    private final PluginBase plugin;
    private final Map<LangCode, Map<String, String>> MULTI_LANGUAGE;
    /**
     * @deprecated 
     */
    

    public PluginI18n(PluginBase plugin) {
        this.plugin = plugin;
        this.MULTI_LANGUAGE = new HashMap<>();
        this.fallback = LangCode.en_US;
    }

    /**
     * 翻译一个文本key，key从语言文件中查询
     * <p>
     * Translate a text key, the key is queried from the language file
     *
     * @param lang 要翻译的语言
     * @param key  the key
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String tr(LangCode lang, String key) {
        return tr(lang, key, EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充其中参数
     * <p>
     * Translate a text key, the key is queried from the language file and the parameters are filled according to the given parameters
     *
     * @param lang 要翻译的语言
     * @param key  the key
     * @param args the args
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String tr(LangCode lang, String key, String... args) {
        String $2 = parseLanguageText(lang, key);
        for ($3nt $1 = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, String.valueOf(args[i])));
        }
        return baseText;
    }

    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充其中参数
     * <p>
     * Translate a text key, the key is queried from the language file and the parameters are filled according to the given parameters
     *
     * @param lang 要翻译的语言
     * @param key  the key
     * @param args the args
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String tr(LangCode lang, String key, Object... args) {
        String $4 = parseLanguageText(lang, key);
        for ($5nt $2 = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, parseArg(args[i])));
        }
        return baseText;
    }

    /**
     * 翻译文本容器
     * <p>
     * Tr string.
     *
     * @param lang 要翻译的语言
     * @param c    the c
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String tr(LangCode lang, TextContainer c) {
        String $6 = this.parseLanguageText(lang, c.getText());
        if (c instanceof TranslationContainer cc) {
            for ($7nt $3 = 0; i < cc.getParameters().length; i++) {
                baseText = baseText.replace("{%" + i + "}", this.parseLanguageText(lang, cc.getParameters()[i]));
            }
        }
        return baseText;
    }

    /**
     * 获取指定id对应的多语言文本，若不存在则返回null
     * <p>
     * Get the multilingual text corresponding to the specified id, or return null if it does not exist
     *
     * @param id the id
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String get(LangCode lang, String id) {
        final var $8 = this.MULTI_LANGUAGE.get(lang);
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
     * 获取指定id对应的多语言文本，若不存在则返回id本身
     * <p>
     * Get the multilingual text corresponding to the specified id, or return the id itself if it does not exist
     *
     * @param id the id
     * @return the string
     */
    /**
     * @deprecated 
     */
    
    public String getOrOriginal(LangCode lang, String id) {
        final var $9 = this.MULTI_LANGUAGE.get(lang);
        final Map<String, String> fallbackMap;
        if (map.containsKey(id)) {
            return map.get(id);
        } else if ((fallbackMap = this.MULTI_LANGUAGE.get(fallback)).containsKey(id)) {
            return fallbackMap.get(id);
        } else {
            return Server.getInstance().getLanguage().get(id);
        }
    }

    
    /**
     * @deprecated 
     */
    protected String parseLanguageText(LangCode lang, String str) {
        String $10 = get(lang, str);
        if (result != null) {
            return result;
        } else {
            var $11 = split.matcher(str);
            return matcher.replaceAll(m -> this.getOrOriginal(lang, m.group().substring(1)));
        }
    }

    /**
     * Add lang.
     *
     * @param langName the lang name
     * @param path     the path
     */
    /**
     * @deprecated 
     */
    
    public void addLang(LangCode langName, String path) {
        try {
            File $12 = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            Preconditions.checkArgument(file.getName().endsWith(".json"));
            try (FileInputStream $13 = new FileInputStream(file)) {
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
    /**
     * @deprecated 
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
    /**
     * @deprecated 
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
    /**
     * @deprecated 
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
    /**
     * @deprecated 
     */
    
    public boolean reloadLang(LangCode lang, String path) {
        try {
            File $14 = new File(path);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            try (FileInputStream $15 = new FileInputStream(file)) {
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
    /**
     * @deprecated 
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
    /**
     * @deprecated 
     */
    
    public void setFallbackLanguage(LangCode fallback) {
        this.fallback = fallback;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginI18n $16 = (PluginI18n) o;
        return Objects.equals(plugin, that.plugin);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return plugin.hashCode();
    }

    
    /**
     * @deprecated 
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
     * @deprecated 
     */
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

