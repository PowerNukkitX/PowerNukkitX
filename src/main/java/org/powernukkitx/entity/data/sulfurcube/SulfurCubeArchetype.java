package org.powernukkitx.entity.data.sulfurcube;

import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.item.Item;
import org.powernukkitx.tags.ItemTags;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Movement profile a sulfur cube takes on once it swallows a block.
 * <p>
 * Which block maps to which profile is not hardcoded here, it comes from the vanilla
 * {@code minecraft:sulfur_cube_archetype_*} item tags shipped in gamedata.
 */
public enum SulfurCubeArchetype {
    BOUNCY("bouncy", 2.0f, 0.3f, 0.01f, 0.33f, 0.07f, true),
    REGULAR("regular", 1.0f, 0.3f, 0.1f, 0.33f, 0.06f, true),
    SLOW_BOUNCY("slow_bouncy", -0.4f, 0.3f, 0.05f, 0.33f, 0.16f, true),
    SLOW_FLAT("slow_flat", -0.5f, 0.4f, 0.1f, 0.33f, 0.07f, false),
    FAST_FLAT("fast_flat", 1.0f, 0.2f, 0.01f, 0.73f, 0.06f, false),
    LIGHT("light", 1.0f, 0.3f, 1.8f, 0.33f, 0.12f, true),
    FAST_SLIDING("fast_sliding", -0.5f, 0.05f, 0.01f, 0.53f, 0.06f, false),
    SLOW_SLIDING("slow_sliding", -0.8f, 0.05f, 0.01f, 0.33f, 0.06f, false),
    STICKY("sticky", 2.0f, 2.0f, 0.01f, 0.33f, 0.06f, false),
    HIGH_RESISTANCE("high_resistance", -0.7f, 1.0f, 0.01f, 0.33f, 0.06f, false),
    EXPLOSIVE("explosive", 1.0f, 0.3f, 0.3f, 0.33f, 0.06f, true),
    HOT("hot", 1.0f, 0.3f, 0.1f, 0.33f, 0.06f, true);

    /**
     * Value the {@code minecraft:sulfur_cube_archetype} entity property takes when nothing is swallowed.
     */
    public static final String NONE = "none";

    private static final String PROPERTY_IDENTIFIER = "minecraft:sulfur_cube_archetype";

    private static final Map<String, SulfurCubeArchetype> BY_ITEM_ID = new HashMap<>();

    @Getter
    private final String propertyName;
    @Getter
    private final String itemTag;
    private final float speed;
    @Getter
    private final float horizontalPower;
    @Getter
    private final float verticalPower;
    /**
     * How much of the cube's horizontal motion survives a tick while it is on the ground.
     */
    @Getter
    private final float groundKeepPerTick;
    /**
     * How much of the cube's horizontal motion survives a tick while it is airborne.
     */
    @Getter
    private final float airKeepPerTick;
    /**
     * Whether the cube is buoyant enough to bob on top of a fluid.
     */
    @Getter
    @Accessors(fluent = true)
    private final boolean floats;

    SulfurCubeArchetype(String propertyName, float speed, float friction, float airDrag,
                        float horizontalPower, float verticalPower, boolean floats) {
        this.propertyName = propertyName;
        this.itemTag = PROPERTY_IDENTIFIER + "_" + propertyName;
        this.speed = speed;
        this.horizontalPower = horizontalPower;
        this.verticalPower = verticalPower;
        this.groundKeepPerTick = friction >= 1f ? 0f : (float) Math.pow(1 - friction, 1 / 20d);
        this.airKeepPerTick = airDrag >= 1f ? 0f : (float) Math.pow(1 - airDrag, 1 / 20d);
        this.floats = floats;
    }

    public float getKnockbackFactor() {
        return Math.max(0f, 1f + speed);
    }

    /**
     * Declares the archetype entity property, listing {@link #NONE} plus every constant in declaration order.
     */
    @NotNull
    public static EnumEntityProperty entityProperty() {
        String[] enumValues = new String[values().length + 1];
        enumValues[0] = NONE;
        for (SulfurCubeArchetype archetype : values()) {
            enumValues[archetype.ordinal() + 1] = archetype.propertyName;
        }
        return new EnumEntityProperty(PROPERTY_IDENTIFIER, enumValues, NONE, true);
    }

    /**
     * Profile for a block already sitting inside a cube. Anything the archetype tags do not cover still has to
     * move somehow, so it falls back to {@link #REGULAR} rather than reading as an empty cube.
     */
    @Nullable
    public static SulfurCubeArchetype forSwallowed(@Nullable String identifier) {
        if (identifier == null) return null;
        SulfurCubeArchetype tagged = fromIdentifier(identifier);
        return tagged != null ? tagged : REGULAR;
    }

    @Nullable
    public static SulfurCubeArchetype fromItem(@Nullable Item item) {
        if (item == null || !item.isBlock()) return null;
        SulfurCubeArchetype byItemId = fromIdentifier(item.getId());
        return byItemId != null ? byItemId : fromIdentifier(item.getBlockId());
    }

    @Nullable
    public static SulfurCubeArchetype fromIdentifier(@Nullable String identifier) {
        return identifier == null ? null : BY_ITEM_ID.get(identifier);
    }

    static {
        for (SulfurCubeArchetype archetype : values()) {
            for (String identifier : ItemTags.getItemSet(archetype.itemTag)) {
                BY_ITEM_ID.put(identifier, archetype);
            }
        }
    }
}
