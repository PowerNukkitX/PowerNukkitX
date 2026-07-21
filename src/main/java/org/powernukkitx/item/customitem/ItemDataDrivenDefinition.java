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
 * Use {@link ItemDataDrivenBuilder} to create instances.
 */
public class ItemDataDrivenDefinition extends ItemCustomDefinition {
    private final String identifier;
    private final CompoundTag nbt;

    ItemDataDrivenDefinition(@NotNull String identifier, @NotNull CompoundTag components) {
        super(ItemCustomVersion.DATA_DRIVEN);
        this.identifier = identifier;
        this.components.putAll(components);
        this.nbt = buildNBT();
    }

    /**
     * Get the item identifier.
     */
    public @NotNull String getIdentifier() {
        return identifier;
    }

    /**
     * Get the full NBT definition (format_version + components).
     */
    public @NotNull CompoundTag getNBT() {
        return nbt.copy();
    }

    @Override
    public @NotNull CompoundTag toNetwork() {
        return components;
    }

    private CompoundTag buildNBT() {
        CompoundTag root = new CompoundTag();
        root.putString("format_version", "1.21.50");
        CompoundTag item = new CompoundTag();
        item.putString("identifier", identifier);
        item.putCompound("components", components);
        root.putCompound("minecraft:item", item);
        return root;
    }
}