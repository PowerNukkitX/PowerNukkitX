package cn.nukkit.lang;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of supported language codes and their display names for localization.
 * <p>
 * This enum defines all language codes supported by the application, each associated with a human-readable display name.
 * It provides utility methods for converting between string codes and enum values, and for retrieving the display name.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Defines language codes for all supported locales.</li>
 *   <li>Associates each code with a display name in its native language.</li>
 *   <li>Provides a static method to safely parse a string to a LangCode, returning null if not found.</li>
 *   <li>Overrides {@link #toString()} to return the display name.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     LangCode code = LangCode.from("fr_FR");
 *     if (code != null) {
 *         System.out.println(code); // Prints: Français (France)
 *     }
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This enum is thread-safe as all enums in Java are inherently thread-safe.
 * </p>
 *
 * @author PowerNukkitX Team
 * @since 1.0
 */
@Slf4j
public enum LangCode {
    en_US("English (United States)"),
    en_GB("English (United Kingdom)"),
    de_DE("Deutsch (Deutschland)"),
    es_ES("Español (España)"),
    es_MX("Español (México)"),
    fr_FR("Français (France)"),
    fr_CA("Français (Canada)"),
    it_IT("Italiano (Italia)"),
    ja_JP("日本語 (日本)"),
    ko_KR("한국어 (대한민국)"),
    pt_BR("Português (Brasil)"),
    pt_PT("Português (Portugal)"),
    ru_RU("Русский (Россия)"),
    zh_CN("中文(简体)"),
    zh_TW("中文(繁體)"),
    nl_NL("Nederlands (Nederland)"),
    bg_BG("Български (България)"),
    cs_CZ("Čeština (Česko)"),
    da_DK("Dansk (Danmark)"),
    el_GR("Ελληνικά (Ελλάδα)"),
    fi_FI("Suomi (Suomi)"),
    hu_HU("Magyar (Magyarország)"),
    id_ID("Indonesia (Indonesia)"),
    nb_NO("Norsk bokmål (Norge)"),
    pl_PL("Polski (Polska)"),
    sk_SK("Slovenčina (Slovensko)"),
    sv_SE("Svenska (Sverige)"),
    tr_TR("Türkçe (Türkiye)"),
    uk_UA("Українська (Україна)"),
    vi_VN("Tiếng Việt (Việt Nam)"),
    lt_LT("Lietuvietis (Lithuania)");
    tl_PH("Filipino (Pilipinas)");

    /**
     * The display name of the language, in its native language.
     */
    private final String string;

    /**
     * Constructs a LangCode enum constant with the specified display name.
     *
     * @param string the display name of the language, in its native language
     */
    LangCode(String string) {
        this.string = string;
    }

    /**
     * Returns the display name of the language, in its native language.
     *
     * @return the display name of the language
     */
    @Override
    public String toString() {
        return this.string;
    }

    /**
     * Returns the LangCode enum constant corresponding to the given name, or null if not found.
     *
     * @param name the name of the language code (e.g., "fr_FR")
     * @return the corresponding LangCode, or null if not found
     */
    @Nullable
    public static LangCode from(String name) {
        try {
            return valueOf(LangCode.class, name);
        } catch (IllegalArgumentException ignore) {
            log.error("Can't find LangCode for {},return null", name);
            return null;
        }
    }
}
