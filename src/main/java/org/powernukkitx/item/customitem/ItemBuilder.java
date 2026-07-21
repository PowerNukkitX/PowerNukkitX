package org.powernukkitx.item.customitem;

import org.powernukkitx.item.customitem.component.*;
import org.powernukkitx.item.customitem.legacy.components.*;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.*;

/**
 * Unified fluent builder for creating custom item definitions.
 * <p>
 * This builder acts as a facade that delegates to either
 * {@link ItemDataDrivenBuilder} (version 2) or {@link ItemLegacyBuilder} (version 1)
 * based on the version specified.
 * <p>
 * <b>DataDriven (recommended):</b> Full component system, all Bedrock components available.<br>
 * <b>Legacy:</b> Flat key-value components, limited set of properties.
 * <p>
 * Example:
 * <pre>{@code
 * // Data-driven (default)
 * ItemDataDrivenDefinition def = ItemBuilder.create("myplugin:my_item")
 *     .name("My Item")
 *     .icon("my_texture")
 *     .maxStackSize(16)
 *     .build();
 *
 * // Legacy
 * ItemLegacyDefinition def = ItemBuilder.createLegacy("myplugin:my_item")
 *     .maxDamage(250)
 *     .handEquipped(true)
 *     .foil(true)
 *     .build();
 * }</pre>
 */
public class ItemBuilder {
    private final String identifier;
    private ItemCustomVersion version = ItemCustomVersion.DATA_DRIVEN;

    // Data-driven fields
    private final Map<ItemComponentIds, ItemComponent> dataDrivenComponents = new LinkedHashMap<>();
    private String name = "";
    private String icon = "";
    private int maxStackSize = 64;
    private int maxDamage = 0;
    private boolean handEquipped = false;
    private boolean foil = false;
    private boolean hideDurability = false;
    private float damage = 0;
    private float useDuration = 0;
    private float nutrition = 0;
    private float saturation = 0;
    private boolean canAlwaysEat = false;
    private String creativeCategory = "nature";
    private String creativeGroup = "";
    private String blockStateName = "";
    private int cooldownTicks = 0;
    private String cooldownCategory = "";
    private String equipSlot = "";
    private int enchantValue = 0;
    private String enchantSlot = "";

    // Legacy fields
    private final List<ItemLegacyComponent> legacyComponents = new ArrayList<>();
    private boolean legacyFoil = false;
    private boolean legacyHandEquipped = false;
    private int legacyMaxDamage = 0;
    private int legacyMaxStackSize = 64;

    private ItemBuilder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Create a new item builder (defaults to data-driven version).
     */
    public static ItemBuilder create(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        return new ItemBuilder(identifier);
    }

    /**
     * Create a new legacy item builder.
     */
    public static ItemBuilder createLegacy(String identifier) {
        ItemBuilder builder = create(identifier);
        builder.version = ItemCustomVersion.LEGACY;
        return builder;
    }

    // ---- Version selection ----

    /**
     * Set the item version explicitly.
     */
    public ItemBuilder version(ItemCustomVersion version) {
        this.version = version;
        return this;
    }

    /**
     * Use data-driven version (recommended).
     */
    public ItemBuilder dataDriven() {
        this.version = ItemCustomVersion.DATA_DRIVEN;
        return this;
    }

    /**
     * Use legacy version.
     */
    public ItemBuilder legacy() {
        this.version = ItemCustomVersion.LEGACY;
        return this;
    }

    // ---- Common shorthand methods ----

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder icon(String texture) {
        this.icon = texture;
        return this;
    }

    public ItemBuilder maxStackSize(int size) {
        this.maxStackSize = Math.max(1, Math.min(64, size));
        return this;
    }

    public ItemBuilder maxDamage(int damage) {
        this.maxDamage = Math.max(0, damage);
        return this;
    }

    public ItemBuilder durability(int value) {
        this.maxDamage = Math.max(0, value);
        return this;
    }

    public ItemBuilder handEquipped(boolean value) {
        this.handEquipped = value;
        return this;
    }

    public ItemBuilder foil(boolean value) {
        this.foil = value;
        return this;
    }

    public ItemBuilder hideDurability(boolean value) {
        this.hideDurability = value;
        return this;
    }

    public ItemBuilder damage(float value) {
        this.damage = value;
        return this;
    }

    public ItemBuilder useDuration(float value) {
        this.useDuration = value;
        return this;
    }

    public ItemBuilder food(float nutrition, float saturation) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        return this;
    }

    public ItemBuilder canAlwaysEat(boolean value) {
        this.canAlwaysEat = value;
        return this;
    }

    public ItemBuilder creativeCategory(String category) {
        this.creativeCategory = category;
        return this;
    }

    public ItemBuilder creativeGroup(String group) {
        this.creativeGroup = group;
        return this;
    }

    public ItemBuilder block(String blockStateName) {
        this.blockStateName = blockStateName;
        return this;
    }

    public ItemBuilder cooldown(String category, int ticks) {
        this.cooldownCategory = category;
        this.cooldownTicks = ticks;
        return this;
    }

    public ItemBuilder equippable(String slot) {
        this.equipSlot = slot;
        return this;
    }

    public ItemBuilder enchantable(int value, String slot) {
        this.enchantValue = value;
        this.enchantSlot = slot;
        return this;
    }

    // ---- Data-driven component methods ----

    public ItemBuilder component(ItemComponent component) {
        dataDrivenComponents.put(component.getId(), component);
        return this;
    }

    public ItemBuilder removeComponent(ItemComponentIds id) {
        dataDrivenComponents.remove(id);
        return this;
    }

    public boolean hasComponent(ItemComponentIds id) {
        return dataDrivenComponents.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemComponent> T getComponent(ItemComponentIds id) {
        return (T) dataDrivenComponents.get(id);
    }

    // ---- Legacy component methods ----

    public ItemBuilder legacyComponent(ItemLegacyComponent component) {
        legacyComponents.add(component);
        return this;
    }

    // ---- Build ----

    /**
     * Build the item definition.
     *
     * @return the built definition (DataDrivenDefinition or LegacyDefinition)
     */
    public ItemCustomDefinition build() {
        if (version == ItemCustomVersion.LEGACY) {
            return buildLegacy();
        }
        return buildDataDriven();
    }

    /**
     * Build as a data-driven definition.
     */
    public ItemDataDrivenDefinition buildDataDriven() {
        ItemDataDrivenBuilder builder = ItemDataDrivenBuilder.create(identifier);
        applyDataDrivenShorthand(builder);
        for (ItemComponent comp : dataDrivenComponents.values()) {
            builder.component(comp);
        }
        return builder.build();
    }

    /**
     * Build as a legacy definition.
     */
    public ItemLegacyDefinition buildLegacy() {
        ItemLegacyBuilder builder = ItemLegacyBuilder.create(identifier);
        applyLegacyShorthand(builder);
        for (ItemLegacyComponent comp : legacyComponents) {
            builder.component(comp);
        }
        return builder.build();
    }

    private void applyDataDrivenShorthand(ItemDataDrivenBuilder builder) {
        // Components
        if (name != null && !name.isBlank()) builder.name(name);
        if (icon != null && !icon.isBlank()) builder.icon(icon);
        builder.maxStackSize(maxStackSize);
        if (maxDamage > 0) builder.maxDamage(maxDamage);
        if (handEquipped) builder.handEquipped(true);
        if (foil) builder.foil(true);
        if (hideDurability) builder.hideDurability(true);
        if (damage > 0) builder.damage(damage);
        if (useDuration > 0) builder.useDuration(useDuration);
        if (nutrition > 0) builder.food(nutrition, saturation);
        if (canAlwaysEat) builder.canAlwaysEat(true);
        if (blockStateName != null && !blockStateName.isBlank()) builder.block(blockStateName);
        if (cooldownTicks > 0) builder.cooldown(cooldownCategory, cooldownTicks);
        if (equipSlot != null && !equipSlot.isBlank()) builder.equippable(equipSlot);
        if (enchantValue > 0) builder.enchantable(enchantValue, enchantSlot);

        // Properties
        if (creativeCategory != null && !creativeCategory.isBlank()) {
            builder.creativeCategory(creativeCategory);
        }
        if (creativeGroup != null && !creativeGroup.isBlank()) {
            builder.creativeGroup(creativeGroup);
        }
    }

    private void applyLegacyShorthand(ItemLegacyBuilder builder) {
        if (maxDamage > 0) builder.maxDamage(maxDamage);
        if (handEquipped) builder.handEquipped(true);
        if (foil) builder.foil(true);
        builder.maxStackSize(maxStackSize);
    }
}