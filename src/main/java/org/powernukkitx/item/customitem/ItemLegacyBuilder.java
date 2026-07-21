package org.powernukkitx.item.customitem;

import org.powernukkitx.item.customitem.legacy.components.*;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.*;

/**
 * Fluent builder for creating legacy item definitions (version 1).
 * <p>
 * Legacy items use the older Bedrock item component system with a limited
 * set of flat components. This is the approach used by older plugins
 * and for backward compatibility.
 * <p>
 * Example:
 * <pre>{@code
 * ItemLegacyDefinition def = ItemLegacyBuilder.create("myplugin:my_item")
 *     .maxDamage(250)
 *     .handEquipped(true)
 *     .foil(true)
 *     .maxStackSize(1)
 *     .build();
 * }</pre>
 */
public class ItemLegacyBuilder {
    private final String identifier;
    private final List<ItemLegacyComponent> legacyComponents = new ArrayList<>();

    // Shorthand values
    private int maxDamage = 0;
    private boolean handEquipped = false;
    private boolean foil = false;
    private boolean stackedByData = false;
    private float useDuration = 0;
    private int maxStackSize = 64;
    private float foodNutrition = 0;
    private float foodSaturation = 0;
    private boolean canAlwaysEat = false;
    private String seedCropResult = "";
    private float seedGrowth = 0;
    private String blockStateName = "";
    private boolean isCamera = false;

    private ItemLegacyBuilder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Create a new legacy item builder.
     */
    public static ItemLegacyBuilder create(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        return new ItemLegacyBuilder(identifier);
    }

    // ---- Shorthand methods ----

    /**
     * Set the maximum damage (durability). 0 = no durability.
     */
    public ItemLegacyBuilder maxDamage(int damage) {
        this.maxDamage = Math.max(0, damage);
        return this;
    }

    /**
     * Set durability (alias for maxDamage).
     */
    public ItemLegacyBuilder durability(int value) {
        this.maxDamage = Math.max(0, value);
        return this;
    }

    /**
     * Set whether the item renders like a tool in hand.
     */
    public ItemLegacyBuilder handEquipped(boolean value) {
        this.handEquipped = value;
        return this;
    }

    /**
     * Set whether the item has enchantment glint.
     */
    public ItemLegacyBuilder foil(boolean value) {
        this.foil = value;
        return this;
    }

    /**
     * Set whether the item is stackable by data value.
     */
    public ItemLegacyBuilder stackedByData(boolean value) {
        this.stackedByData = value;
        return this;
    }

    /**
     * Set the use duration in ticks.
     */
    public ItemLegacyBuilder useDuration(float ticks) {
        this.useDuration = ticks;
        return this;
    }

    /**
     * Set the maximum stack size.
     */
    public ItemLegacyBuilder maxStackSize(int size) {
        this.maxStackSize = Math.max(1, Math.min(64, size));
        return this;
    }

    /**
     * Set food properties.
     */
    public ItemLegacyBuilder food(float nutrition, float saturation) {
        this.foodNutrition = nutrition;
        this.foodSaturation = saturation;
        return this;
    }

    /**
     * Set whether the food can always be eaten.
     */
    public ItemLegacyBuilder canAlwaysEat(boolean value) {
        this.canAlwaysEat = value;
        return this;
    }

    /**
     * Set seed properties.
     */
    public ItemLegacyBuilder seed(String cropResult, float growth) {
        this.seedCropResult = cropResult;
        this.seedGrowth = growth;
        return this;
    }

    /**
     * Set the block state name this item places when used.
     */
    public ItemLegacyBuilder block(String blockStateName) {
        this.blockStateName = blockStateName;
        return this;
    }

    /**
     * Set whether this is a camera item.
     */
    public ItemLegacyBuilder camera(boolean value) {
        this.isCamera = value;
        return this;
    }

    // ---- Add raw legacy components ----

    /**
     * Add a raw legacy component.
     */
    public ItemLegacyBuilder component(ItemLegacyComponent component) {
        legacyComponents.add(component);
        return this;
    }

    // ---- Build ----

    public ItemLegacyDefinition build() {
        ItemLegacyDefinition def = new ItemLegacyDefinition(identifier);

        // Add shorthand components
        if (maxDamage > 0) {
            def.addComponent(new LegacyMaxDamageComponent(maxDamage));
        }
        if (handEquipped) {
            def.addComponent(new LegacyHandEquippedComponent());
        }
        if (foil) {
            def.addComponent(new LegacyFoilComponent());
        }

        // Add raw components
        for (ItemLegacyComponent comp : legacyComponents) {
            def.addComponent(comp);
        }

        return def;
    }
}