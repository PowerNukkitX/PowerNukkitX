package cn.nukkit.item.trim;

/**
 * @author glorydark
 * @date {2023/8/9} {18:23}
 */
public enum ItemArmorPatternType {

    COAST_ARMOR_TRIM("coast"),
    DUNE_ARMOR_TRIM("dune"),
    EYE_ARMOR_TRIM("eye"),
    RIB_ARMOR_TRIM("rib"),
    SENTRY_ARMOR_TRIM("sentry"),
    SNOUT_ARMOR_TRIM("snout"),
    SPIRE_ARMOR_TRIM("spire"),
    TIDE_ARMOR_TRIM("tide"),
    VEX_ARMOR_TRIM("vex"),
    WARD_ARMOR_TRIM("ward"),
    WILD_ARMOR_TRIM("wild"),
    HOST_ARMOR_TRIM("host"),
    RAISER_ARMOR_TRIM_ARMOR_TRIM("raiser"),
    SHAPER_ARMOR_TRIM("shaper"),
    SILENCE_ARMOR_TRIM("silence"),
    WAYFINDER_ARMOR_TRIM("wayfinder");

    private final String trimPattern;

    ItemArmorPatternType(String name)
    {
        this.trimPattern = name;
    }


}
