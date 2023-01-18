
package cn.nukkit.lang;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@PowerNukkitXOnly
@Since("1.19.50-r4")
@Log4j2
public class PluginI18n {
    /**
     * 插件多语言的默认备选语言
     */
    private LangCode fallback;
    private final Pattern split = Pattern.compile("%[A-Za-z0-9_.-]+");
    private final String pluginName;
    private final Map<LangCode, Map<String, String>> MULTI_LANGUAGE;

    public PluginI18n(String pluginName) {
        this.pluginName = pluginName;
        this.MULTI_LANGUAGE = new HashMap<>();
        this.fallback = LangCode.en_US;
    }


    /**
     * 翻译一个文本key，key从语言文件中查询
     *
     * @param key the key
     * @return the string
     */
    public String tr(LangCode lang, String key) {
        return tr(lang, key, EmptyArrays.EMPTY_STRINGS);
    }


    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充结果
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    public String tr(LangCode lang, String key, String... args) {
        String baseText = parseLanguageText(lang, key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, String.valueOf(args[i])));
        }
        return baseText;
    }


    /**
     * 翻译一个文本key，key从语言文件中查询，并且按照给定参数填充结果
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    public String tr(LangCode lang, String key, Object... args) {
        String baseText = parseLanguageText(lang, key);
        for (int i = 0; i < args.length; i++) {
            baseText = baseText.replace("{%" + i + "}", parseLanguageText(lang, parseArg(args[i])));
        }
        return baseText;
    }

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
     * 获取指定id对应的多语言文本，若不存在则返回null
     *
     * @param id the id
     * @return the string
     */
    public String get(LangCode lang, String id) {
        final var map = this.MULTI_LANGUAGE.get(lang);
        final Map<String, String> fallbackMap;
        if (map.containsKey(id)) {
            return map.get(id);
        } else if ((fallbackMap = this.MULTI_LANGUAGE.get(fallback)).containsKey(id)) {
            return fallbackMap.get(id);
        } else {
            return Server.getInstance().getLanguage().internalGet(id);
        }
    }


    /**
     * 获取指定id对应的多语言文本，若不存在则返回id本身
     *
     * @param id the id
     * @return the string
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
            return matcher.replaceAll(m -> this.get(lang, m.group().substring(1)));
        }
    }

    public void addLang(LangCode langName, String path) {
        try {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException();
            }
            try (FileInputStream stream = new FileInputStream(file)) {
                this.MULTI_LANGUAGE.put(langName, parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
            }
        } catch (IOException e) {
            log.fatal("Failed to load language at {}", path, e);
        }
    }

    public void addLang(LangCode langName, InputStream stream) {
        try {
            this.MULTI_LANGUAGE.put(langName, parseLang(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
        } catch (IOException e) {
            log.error("Failed to parse the language input stream", e);
        }
    }

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
            log.fatal("Failed to load language at {}", path, e);
            return false;
        }
    }

    public boolean reloadLang(LangCode lang, InputStream stream) {
        return reloadLang(lang, new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }

    public LangCode getFallbackLanguage() {
        return fallback;
    }

    public void setFallbackLanguage(LangCode fallback) {
        this.fallback = fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginI18n that = (PluginI18n) o;
        return Objects.equals(pluginName, that.pluginName);
    }

    @Override
    public int hashCode() {
        return pluginName.hashCode();
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
        try {
            readAndWriteLang(reader, d);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> parseLang(BufferedReader reader) throws IOException {
        Map<String, String> d = new Object2ObjectOpenHashMap<>();
        readAndWriteLang(reader, d);
        return d;
    }

    static void readAndWriteLang(BufferedReader reader, Map<String, String> d) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }
            String[] t = line.split("=", 2);
            if (t.length < 2) {
                continue;
            }
            String key = t[0];
            String value = t[1];
            if (value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                value = value.substring(1, value.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
            }
            if (value.isEmpty()) {
                continue;
            }
            d.put(key, value);
        }
    }
}

