package org.powernukkitx.item.customitem;

import org.powernukkitx.item.customitem.component.*;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;

import java.util.*;

/**
 * Fluent builder for creating custom item definitions.
 * <p>
 * Components can be added individually or via shorthand methods.
 * The builder produces an {@link ItemDefinition} that can be registered
 * with the server.
 * <p>
 * Example:
 * <pre>{@code
 * ItemDefinition def = ItemBuilder.create("myplugin:my_item")
 *     .name("My Custom Item")
 *     .icon("my_texture")
 *     .maxStackSize(16)
 *     .handEquipped(true)
 *     .foil(true)
 *     .damage(5.0f)
 *     .durability(250)
 *     .food(4, 2.5f)
 *     .build();
 * }</pre>
 */
public class ItemBuilder {
    private final String identifier;
    private final Map<ItemComponentIds, ItemComponent> components = new LinkedHashMap<>();
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
    private boolean isStackableByData = false;
    private int useOnClickCooldown = 0;

    private ItemBuilder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Create a new ItemBuilder for the given identifier.
     */
    public static ItemBuilder create(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        return new ItemBuilder(identifier);
    }

    // ---- Shorthand component methods ----

    /**
     * Set the display name of the item.
     */
    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the texture icon name.
     */
    public ItemBuilder icon(String texture) {
        this.icon = texture;
        return this;
    }

    /**
     * Set the maximum stack size (1-64).
     */
    public ItemBuilder maxStackSize(int size) {
        this.maxStackSize = Math.max(1, Math.min(64, size));
        return this;
    }

    /**
     * Set the maximum durability (damage value). 0 = no durability.
     */
    public ItemBuilder maxDamage(int damage) {
        this.maxDamage = Math.max(0, damage);
        return this;
    }

    /**
     * Set durability (alias for maxDamage).
     */
    public ItemBuilder durability(int value) {
        this.maxDamage = Math.max(0, value);
        return this;
    }

    /**
     * Set whether the item renders like a tool in hand.
     */
    public ItemBuilder handEquipped(boolean value) {
        this.handEquipped = value;
        return this;
    }

    /**
     * Set whether the item has enchantment glint.
     */
    public ItemBuilder foil(boolean value) {
        this.foil = value;
        return this;
    }

    /**
     * Set whether to hide the durability bar.
     */
    public ItemBuilder hideDurability(boolean value) {
        this.hideDurability = value;
        return this;
    }

    /**
     * Set attack damage dealt by the item.
     */
    public ItemBuilder damage(float value) {
        this.damage = value;
        return this;
    }

    /**
     * Set the use duration in seconds.
     */
    public ItemBuilder useDuration(float value) {
        this.useDuration = value;
        return this;
    }

    /**
     * Set food properties.
     */
    public ItemBuilder food(float nutrition, float saturation) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        return this;
    }

    /**
     * Set whether the food can always be eaten.
     */
    public ItemBuilder canAlwaysEat(boolean value) {
        this.canAlwaysEat = value;
        return this;
    }

    /**
     * Set the creative category in the inventory.
     */
    public ItemBuilder creativeCategory(String category) {
        this.creativeCategory = category;
        return this;
    }

    /**
     * Set the creative group in the inventory.
     */
    public ItemBuilder creativeGroup(String group) {
        this.creativeGroup = group;
        return this;
    }

    /**
     * Set the block state name this item places when used.
     */
    public ItemBuilder block(String blockStateName) {
        this.blockStateName = blockStateName;
        return this;
    }

    /**
     * Set whether the item is stackable by data value.
     */
    public ItemBuilder stackedByData(boolean value) {
        this.isStackableByData = value;
        return this;
    }

    /**
     * Set the use on click cooldown.
     */
    public ItemBuilder useOnClickCooldown(int ticks) {
        this.useOnClickCooldown = ticks;
        return this;
    }

    // ---- Generic component methods ----

    /**
     * Add or replace a component.
     */
    public ItemBuilder component(ItemComponent component) {
        components.put(component.getId(), component);
        return this;
    }

    /**
     * Remove a component by ID.
     */
    public ItemBuilder removeComponent(ItemComponentIds id) {
        components.remove(id);
        return this;
    }

    /**
     * Check if a component is present.
     */
    public boolean hasComponent(ItemComponentIds id) {
        return components.containsKey(id);
    }

    /**
     * Get a component by ID.
     */
    @SuppressWarnings("unchecked")
    public <T extends ItemComponent> T getComponent(ItemComponentIds id) {
        return (T) components.get(id);
    }

    // ---- Build ----

    /**
     * Build the ItemDefinition.
     */
    public ItemDefinition build() {
        CompoundTag componentsNbt = buildComponents();
        return new ItemDefinition(identifier, componentsNbt);
    }

    private CompoundTag buildComponents() {
        CompoundTag components = new CompoundTag();

        // Apply shorthand values
        applyIconComponent(components);
        applyDisplayNameComponent(components);
        applyMaxStackSizeComponent(components);
        applyMaxDamageComponent(components);
        applyHandEquippedComponent(components);
        applyFoilComponent(components);
        applyHideDurabilityComponent(components);
        applyDamageComponent(components);
        applyUseDurationComponent(components);
        applyFoodComponent(components);
        applyCreativeCategoryComponent(components);
        applyBlockComponent(components);
        applyStackedByDataComponent(components);

        // Apply explicitly added components
        for (Map.Entry<ItemComponentIds, ItemComponent> entry : components.entrySet()) {
            ItemComponentIds id = entry.getKey();
            if (!components.contains(id.getId())) {
                components.putCompound(id.getId(), entry.getValue().toNBT());
            }
        }

        return components;
    }

    private void applyIconComponent(CompoundTag components) {
        if (icon != null && !icon.isBlank()) {
            components.putCompound("minecraft:icon",
                    new CompoundTag().putString("texture", icon));
        }
    }

    private void applyDisplayNameComponent(CompoundTag components) {
        if (name != null && !name.isBlank()) {
            components.putCompound("minecraft:display_name",
                    new CompoundTag().putString("value", name));
        }
    }

    private void applyMaxStackSizeComponent(CompoundTag components) {
        components.putCompound("minecraft:max_stack_size",
                new CompoundTag().putInt("max_stack_size", maxStackSize));
    }

    private void applyMaxDamageComponent(CompoundTag components) {
        if (maxDamage > 0) {
            components.putCompound("minecraft:max_damage",
                    new CompoundTag().putInt("max_damage", maxDamage));
        }
    }

    private void applyHandEquippedComponent(CompoundTag components) {
        if (handEquipped) {
            components.putCompound("minecraft:hand_equipped",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyFoilComponent(CompoundTag components) {
        if (foil) {
            components.putCompound("minecraft:foil",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyHideDurabilityComponent(CompoundTag components) {
        if (hideDurability) {
            components.putCompound("minecraft:hide_durability",
                    new CompoundTag().putBoolean("value", true));
        }
    }

    private void applyDamageComponent(CompoundTag components) {
        if (damage > 0) {
            components.putCompound("minecraft:damage",
                    new CompoundTag().putFloat("value", damage));
        }
    }

    private void applyUseDurationComponent(CompoundTag components) {
        if (useDuration > 0) {
            components.putCompound("minecraft:use_duration",
                    new CompoundTag().putFloat("use_duration", useDuration));
        }
    }

    private void applyFoodComponent(CompoundTag components) {
        if (nutrition > 0) {
            components.putCompound("minecraft:food",
                    new CompoundTag()
                            .putFloat("nutrition", nutrition)
                            .putFloat("saturation_modifier", saturation)
                            .putBoolean("can_always_eat", canAlwaysEat));
        }
    }

    private void applyCreativeCategoryComponent(CompoundTag components) {
        components.putCompound("minecraft:creative_category",
                new CompoundTag()
                        .putString("category", creativeCategory)
                        .putString("group", creativeGroup));
    }

    private void applyBlockComponent(CompoundTag components) {
        if (blockStateName != null && !blockStateName.isBlank()) {
            components.putCompound("minecraft:block",
                    new CompoundTag().putString("block_state_name", blockStateName));
        }
    }

    private void applyStackedByDataComponent(CompoundTag components) {
        components.putCompound("minecraft:stacked_by_data",
                new CompoundTag().putBoolean("is_stackable", isStackableByData));
    }

    /**
     * ItemDefinition holds the identifier and serialized components NBT.
     */
    public record ItemDefinition(String identifier, CompoundTag components) {
        public CompoundTag toNetwork() {
            return components;
        }
    }
}