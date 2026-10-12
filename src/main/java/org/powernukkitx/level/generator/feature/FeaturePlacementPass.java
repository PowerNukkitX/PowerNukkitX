package org.powernukkitx.level.generator.feature;

import java.util.List;

/**
 * Biome feature placement passes.
 *
 * @author Curse
 */
public enum FeaturePlacementPass {
    PREGENERATION("pregeneration_pass"),
    FIRST("first_pass"),
    BEFORE_UNDERGROUND("before_underground_pass"),
    UNDERGROUND("underground_pass"),
    AFTER_UNDERGROUND("after_underground_pass"),
    BEFORE_SURFACE("before_surface_pass"),
    SURFACE("surface_pass"),
    AFTER_SURFACE("after_surface_pass"),
    BEFORE_SKY("before_sky_pass"),
    SKY("sky_pass"),
    AFTER_SKY("after_sky_pass"),
    FINAL("final_pass");

    private static final List<FeaturePlacementPass> REGULAR = List.of(
            FIRST,
            BEFORE_UNDERGROUND,
            UNDERGROUND,
            AFTER_UNDERGROUND,
            BEFORE_SURFACE,
            SURFACE,
            AFTER_SURFACE,
            BEFORE_SKY,
            SKY,
            AFTER_SKY,
            FINAL
    );

    private final String serializedName;

    FeaturePlacementPass(String serializedName) {
        this.serializedName = serializedName;
    }

    /**
     * Returns the Bedrock serialized placement-pass name.
     */
    public String serializedName() {
        return this.serializedName;
    }

    /**
     * Resolves a Bedrock serialized placement-pass name.
     */
    public static FeaturePlacementPass fromName(String name) {
        if (name == null) {
            return null;
        }

        return switch (name) {
            case "pregeneration_pass" -> PREGENERATION;
            case "first_pass" -> FIRST;
            case "before_underground_pass" -> BEFORE_UNDERGROUND;
            case "underground_pass" -> UNDERGROUND;
            case "after_underground_pass" -> AFTER_UNDERGROUND;
            case "before_surface_pass" -> BEFORE_SURFACE;
            case "surface_pass" -> SURFACE;
            case "after_surface_pass" -> AFTER_SURFACE;
            case "before_sky_pass" -> BEFORE_SKY;
            case "sky_pass" -> SKY;
            case "after_sky_pass" -> AFTER_SKY;
            case "final_pass" -> FINAL;
            default -> null;
        };
    }

    /**
     * Returns the regular Bedrock placement passes in native execution order.
     */
    public static List<FeaturePlacementPass> regular() {
        return REGULAR;
    }
}
