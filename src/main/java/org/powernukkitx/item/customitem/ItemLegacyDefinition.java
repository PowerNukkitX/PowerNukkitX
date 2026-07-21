package org.powernukkitx.item.customitem;

import org.powernukkitx.item.customitem.legacy.components.ItemLegacyComponent;
import org.powernukkitx.item.customitem.legacy.components.LegacyComponentIds;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Legacy item definition (version 1).
 * <p>
 * Uses the older Bedrock item component system where components
 * are flat key-value pairs. Only supports a limited set of components:
 * <ul>
 *   <li>{@code minecraft:max_damage}</li>
 *   <li>{@code minecraft:hand_equipped}</li>
 *   <li>{@code minecraft:foil}</li>
 *   <li>{@code minecraft:stacked_by_data}</li>
 *   <li>{@code minecraft:use_duration}</li>
 *   <li>{@code minecraft:max_stack_size}</li>
 *   <li>{@code minecraft:food}</li>
 *   <li>{@code minecraft:seed}</li>
 *   <li>{@code minecraft:block}</li>
 *   <li>{@code minecraft:camera}</li>
 * </ul>
 * <p>
 * Use {@link ItemLegacyBuilder} to create instances.
 */
public class ItemLegacyDefinition extends ItemCustomDefinition {
    private final String identifier;
    private final Map<LegacyComponentIds, CompoundTag> legacyComponents;

    ItemLegacyDefinition(@NotNull String identifier) {
        super(ItemCustomVersion.LEGACY);
        this.identifier = identifier;
        this.legacyComponents = new LinkedHashMap<>();
    }

    /**
     * Get the item identifier.
     */
    public @NotNull String getIdentifier() {
        return identifier;
    }

    /**
     * Add a legacy component.
     */
    public void addComponent(@NotNull ItemLegacyComponent component) {
        legacyComponents.put(component.getIdentifier(), component.getTag());
        components.putCompound(component.getIdentifier().getStringId(), component.getTag());
    }

    /**
     * Remove a legacy component by ID.
     */
    public void removeComponent(@NotNull LegacyComponentIds id) {
        legacyComponents.remove(id);
        components.remove(id.getStringId());
    }

    /**
     * Check if a legacy component exists.
     */
    public boolean hasComponent(@NotNull LegacyComponentIds id) {
        return legacyComponents.containsKey(id);
    }

    /**
     * Get a legacy component tag.
     */
    public CompoundTag getComponent(@NotNull LegacyComponentIds id) {
        return legacyComponents.get(id);
    }

    /**
     * Get all legacy components as a map.
     */
    public Map<LegacyComponentIds, CompoundTag> getLegacyComponents() {
        return new LinkedHashMap<>(legacyComponents);
    }

    @Override
    public CompoundTag toNetwork() {
        return components;
    }
}