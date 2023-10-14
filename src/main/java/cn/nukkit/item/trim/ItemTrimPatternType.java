package cn.nukkit.item.trim;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author glorydark
 * @date {2023/8/9} {18:23}
 */
@PowerNukkitXOnly
@Since("1.20.30-r2")
public enum ItemTrimPatternType {

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

    @Nullable
    public static ItemTrimMaterialType fromTrimPattern(@NotNull String trimPattern) {
        for (ItemTrimMaterialType value : ItemTrimMaterialType.values()) {
            if (value.getMaterialName().equals(trimPattern)) {
                return value;
            }
        }
        return null;
    }

    private final String trimPattern;

    ItemTrimPatternType(@NotNull String name) {
        this.trimPattern = name;
    }

    @NotNull
    public String getTrimPattern() {
        return trimPattern;
    }

}
