package org.powernukkitx.item.customitem;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Data-driven item definition (version 2).
 * <p>
 * Uses the modern Bedrock component system where each component
 * is serialized as a CompoundTag under the item's component list.
 * This is the recommended approach for new custom items.
 * <p>
 * The NBT structure separates:
 * <ul>
 *   <li><b>Components</b> - Bedrock item components (minecraft:icon, minecraft:max_stack_size, etc.)</li>
 *   <li><b>Properties</b> - Server-side properties (creative_category, is_hidden_in_commands, etc.)</li>
 * </ul>
 * <p>
 * Use {@link ItemDataDrivenBuilder} to create instances.
 */
public class ItemDataDrivenDefinition extends ItemCustomDefinition {
    private final String identifier;
    private final CompoundTag components;
    private final CompoundTag properties;
    private final CompoundTag nbt;

    ItemDataDrivenDefinition(@NotNull String identifier,
                             @NotNull CompoundTag components,
                             @NotNull CompoundTag properties) {
        super(ItemCustomVersion.DATA_DRIVEN);
        this.identifier = identifier;
        this.components = components.copy();
        this.properties = properties.copy();
        this.nbt = buildNBT();
    }

    /**
     * Get the item identifier.
     */
    public @NotNull String getIdentifier() {
        return identifier;
    }

    /**
     * Get the Bedrock components (minecraft:icon, minecraft:max_stack_size, etc.).
     */
    public @NotNull CompoundTag getComponents() {
        return components.copy();
    }

    /**
     * Get the server-side properties (creative_category, is_hidden_in_commands, etc.).
     */
    public @NotNull CompoundTag getProperties() {
        return properties.copy();
    }

    /**
     * Get the full NBT definition (format_version + description + components + properties).
     */
    public @NotNull CompoundTag getNBT() {
        return nbt.copy();
    }

    @Override
    public @NotNull CompoundTag toNetwork() {
        // Merge components and properties for network transmission
        CompoundTag merged = components.copy();
        if (!properties.isEmpty()) {
            merged.putCompound("item_properties", properties);
        }
        return merged;
    }

    private CompoundTag buildNBT() {
        CompoundTag root = new CompoundTag();
        root.putString("format_version", "1.21.50");

        CompoundTag item = new CompoundTag();
        item.putString("identifier", identifier);

        // Description with menu_category
        CompoundTag description = new CompoundTag();
        description.putString("identifier", identifier);
        if (properties.contains("creative_category")) {
            CompoundTag menuCategory = new CompoundTag();
            menuCategory.putString("category", getCategoryName(properties.getInt("creative_category")));
            if (properties.contains("creative_group")) {
                menuCategory.putString("group", properties.getString("creative_group"));
            }
            if (properties.contains("is_hidden_in_commands")) {
                menuCategory.putByte("is_hidden_in_commands", properties.getByte("is_hidden_in_commands"));
            }
            description.putCompound("menu_category", menuCategory);
        }
        item.putCompound("description", description);

        // Bedrock components
        item.putCompound("components", components);

        // Server-side properties (inside components for backward compatibility)
        if (!properties.isEmpty()) {
            CompoundTag comps = item.getCompound("components");
            comps.putCompound("item_properties", properties);
        }

        root.putCompound("minecraft:item", item);
        return root;
    }

    private static String getCategoryName(int categoryId) {
        return switch (categoryId) {
            case 1 -> "construction";
            case 2 -> "nature";
            case 3 -> "items";
            case 4 -> "equipment";
            default -> "items";
        };
    }
}