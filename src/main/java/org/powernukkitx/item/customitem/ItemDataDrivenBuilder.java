package org.powernukkitx.item.customitem;

import org.powernukkitx.item.customitem.component.*;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.*;

/**
 * Fluent builder for creating data-driven item definitions (version 2).
 * <p>
 * This is the modern approach for custom items, using Bedrock's full component system.
 * Each component is serialized individually and can be inspected/modified at runtime.
 * <p>
 * The builder separates:
 * <ul>
 *   <li><b>Components</b> - Bedrock item components (minecraft:icon, minecraft:max_stack_size, etc.)</li>
 *   <li><b>Properties</b> - Server-side properties (creative_category, is_hidden_in_commands, etc.)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * ItemDataDrivenDefinition def = ItemDataDrivenBuilder.create("myplugin:my_sword")
 *     .name("My Sword")
 *     .icon("my_sword")
 *     .maxStackSize(1)
 *     .damage(8.0f)
 *     .durability(250)
 *     .handEquipped(true)
 *     .foil(true)
 *     .creativeCategory("equipment")
 *     .build();
 * }</pre>
 */
public class ItemDataDrivenBuilder {
    private final String identifier;
    private final Map<ItemComponentIds, ItemComponent> components = new LinkedHashMap<>();
    private final CompoundTag properties = new CompoundTag();

    // Shorthand component values
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
    private String blockStateName = "";
    private int cooldownTicks = 0;
    private String cooldownCategory = "";
    private String equipSlot = "";
    private int enchantValue = 0;
    private String enchantSlot = "";

    // Shorthand property values
    private String creativeCategory = "";
    private String creativeGroup = "";
    private boolean hiddenInCommands = false;

    private ItemDataDrivenBuilder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Create a new data-driven item builder.
     */
    public static ItemDataDrivenBuilder create(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        return new ItemDataDrivenBuilder(identifier);
    }

    // ---- Component shorthand methods ----

    public ItemDataDrivenBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemDataDrivenBuilder icon(String texture) {
        this.icon = texture;
        return this;
    }

    public ItemDataDrivenBuilder maxStackSize(int size) {
        this.maxStackSize = Math.max(1, Math.min(64, size));
        return this;
    }

    public ItemDataDrivenBuilder maxDamage(int damage) {
        this.maxDamage = Math.max(0, damage);
        return this;
    }

    public ItemDataDrivenBuilder durability(int value) {
        this.maxDamage = Math.max(0, value);
        return this;
    }

    public ItemDataDrivenBuilder handEquipped(boolean value) {
        this.handEquipped = value;
        return this;
    }

    public ItemDataDrivenBuilder foil(boolean value) {
        this.foil = value;
        return this;
    }

    public ItemDataDrivenBuilder hideDurability(boolean value) {
        this.hideDurability = value;
        return this;
    }

    public ItemDataDrivenBuilder damage(float value) {
        this.damage = value;
        return this;
    }

    public ItemDataDrivenBuilder useDuration(float value) {
        this.useDuration = value;
        return this;
    }

    public ItemDataDrivenBuilder food(float nutrition, float saturation) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        return this;
    }

    public ItemDataDrivenBuilder canAlwaysEat(boolean value) {
        this.canAlwaysEat = value;
        return this;
    }

    public ItemDataDrivenBuilder block(String blockStateName) {
        this.blockStateName = blockStateName;
        return this;
    }

    public ItemDataDrivenBuilder cooldown(String category, int ticks) {
        this.cooldownCategory = category;
        this.cooldownTicks = ticks;
        return this;
    }

    public ItemDataDrivenBuilder equippable(String slot) {
        this.equipSlot = slot;
        return this;
    }

    public ItemDataDrivenBuilder enchantable(int value, String slot) {
        this.enchantValue = value;
        this.enchantSlot = slot;
        return this;
    }

    // ---- Property shorthand methods ----

    /**
     * Set the creative category (construction, nature, items, equipment).
     */
    public ItemDataDrivenBuilder creativeCategory(String category) {
        this.creativeCategory = category;
        return this;
    }

    /**
     * Set the creative group.
     */
    public ItemDataDrivenBuilder creativeGroup(String group) {
        this.creativeGroup = group;
        return this;
    }

    /**
     * Hide from /give and /replaceitem commands.
     */
    public ItemDataDrivenBuilder hiddenInCommands(boolean hidden) {
        this.hiddenInCommands = hidden;
        return this;
    }

    // ---- Generic component methods ----

    /**
     * Add or replace a Bedrock component.
     */
    public ItemDataDrivenBuilder component(ItemComponent component) {
        components.put(component.getId(), component);
        return this;
    }

    /**
     * Remove a Bedrock component by ID.
     */
    public ItemDataDrivenBuilder removeComponent(ItemComponentIds id) {
        components.remove(id);
        return this;
    }

    /**
     * Check if a Bedrock component is present.
     */
    public boolean hasComponent(ItemComponentIds id) {
        return components.containsKey(id);
    }

    /**
     * Get a Bedrock component by ID.
     */
    @SuppressWarnings("unchecked")
    public <T extends ItemComponent> T getComponent(ItemComponentIds id) {
        return (T) components.get(id);
    }

    // ---- Generic property methods ----

    /**
     * Add a server-side property.
     */
    public ItemDataDrivenBuilder property(String key, int value) {
        properties.putInt(key, value);
        return this;
    }

    /**
     * Add a server-side property.
     */
    public ItemDataDrivenBuilder property(String key, String value) {
        properties.putString(key, value);
        return this;
    }

    /**
     * Add a server-side property.
     */
    public ItemDataDrivenBuilder property(String key, boolean value) {
        properties.putBoolean(key, value);
        return this;
    }

    /**
     * Add a server-side property.
     */
    public ItemDataDrivenBuilder property(String key, CompoundTag value) {
        properties.putCompound(key, value);
        return this;
    }

    // ---- Build ----

    public ItemDataDrivenDefinition build() {
        CompoundTag componentsNbt = buildComponents();
        CompoundTag propertiesNbt = buildProperties();
        return new ItemDataDrivenDefinition(identifier, componentsNbt, propertiesNbt);
    }

    private CompoundTag buildComponents() {
        CompoundTag comp = new CompoundTag();

        // Apply shorthand component values
        applyIconComponent(comp);
        applyDisplayNameComponent(comp);
        applyMaxStackSizeComponent(comp);
        applyMaxDamageComponent(comp);
        applyHandEquippedComponent(comp);
        applyFoilComponent(comp);
        applyHideDurabilityComponent(comp);
        applyDamageComponent(comp);
        applyUseDurationComponent(comp);
        applyFoodComponent(comp);
        applyBlockComponent(comp);
        applyCooldownComponent(comp);
        applyEquippableComponent(comp);
        applyEnchantableComponent(comp);

        // Apply explicitly added components
        for (Map.Entry<ItemComponentIds, ItemComponent> entry : components.entrySet()) {
            ItemComponentIds id = entry.getKey();
            if (!comp.contains(id.getId())) {
                comp.putCompound(id.getId(), entry.getValue().toNBT());
            }
        }

        return comp;
    }

    private CompoundTag buildProperties() {
        CompoundTag props = new CompoundTag();

        // Apply shorthand property values
        if (creativeCategory != null && !creativeCategory.isBlank()) {
            props.putInt("creative_category", getCategoryId(creativeCategory));
        }
        if (creativeGroup != null && !creativeGroup.isBlank()) {
            props.putString("creative_group", creativeGroup);
        }
        if (hiddenInCommands) {
            props.putByte("is_hidden_in_commands", (byte) 1);
        }

        return props;
    }

    private void applyIconComponent(CompoundTag comp) {
        if (icon != null && !icon.isBlank()) {
            comp.putCompound("minecraft:icon",
                    new CompoundTag().putString("texture", icon));
        }
    }

    private void applyDisplayNameComponent(CompoundTag comp) {
        if (name != null && !name.isBlank()) {
            comp.putCompound("minecraft:display_name",
                    new CompoundTag().putString("value", name));
        }
    }

    private void applyMaxStackSizeComponent(CompoundTag comp) {
        comp.putCompound("minecraft:max_stack_size",
                new CompoundTag().putInt("max_stack_size", maxStackSize));
    }

    private void applyMaxDamageComponent(CompoundTag comp) {
        if (maxDamage > 0) {
            comp.putCompound("minecraft:max_damage",
                    new CompoundTag().putInt("max_damage", maxDamage));
        }
    }

    private void applyHandEquippedComponent(CompoundTag comp) {
        if (handEquipped) {
            comp.putCompound("minecraft:hand_equipped",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyFoilComponent(CompoundTag comp) {
        if (foil) {
            comp.putCompound("minecraft:foil",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyHideDurabilityComponent(CompoundTag comp) {
        if (hideDurability) {
            comp.putCompound("minecraft:hide_durability",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyDamageComponent(CompoundTag comp) {
        if (damage > 0) {
            comp.putCompound("minecraft:damage",
                    new CompoundTag().putFloat("value", damage));
        }
    }

    private void applyUseDurationComponent(CompoundTag comp) {
        if (useDuration > 0) {
            comp.putCompound("minecraft:use_duration",
                    new CompoundTag().putFloat("use_duration", useDuration));
        }
    }

    private void applyFoodComponent(CompoundTag comp) {
        if (nutrition > 0) {
            comp.putCompound("minecraft:food",
                    new CompoundTag()
                            .putFloat("nutrition", nutrition)
                            .putFloat("saturation_modifier", saturation)
                            .putBoolean("can_always_eat", canAlwaysEat));
        }
    }

    private void applyBlockComponent(CompoundTag comp) {
        if (blockStateName != null && !blockStateName.isBlank()) {
            comp.putCompound("minecraft:block",
                    new CompoundTag().putString("block_state_name", blockStateName));
        }
    }

    private void applyCooldownComponent(CompoundTag comp) {
        if (cooldownTicks > 0) {
            comp.putCompound("minecraft:cooldown",
                    new CompoundTag()
                            .putString("category", cooldownCategory)
                            .putFloat("duration", cooldownTicks / 20.0f));
        }
    }

    private void applyEquippableComponent(CompoundTag comp) {
        if (equipSlot != null && !equipSlot.isBlank()) {
            comp.putCompound("minecraft:equippable",
                    new CompoundTag().putString("slot", equipSlot));
        }
    }

    private void applyEnchantableComponent(CompoundTag comp) {
        if (enchantValue > 0) {
            comp.putCompound("minecraft:enchantable",
                    new CompoundTag()
                            .putInt("value", enchantValue)
                            .putString("slot", enchantSlot));
        }
    }

    private static int getCategoryId(String category) {
        return switch (category.toLowerCase(Locale.ROOT)) {
            case "construction" -> 1;
            case "nature" -> 2;
            case "items" -> 3;
            case "equipment" -> 4;
            default -> 3;
        };
    }
}