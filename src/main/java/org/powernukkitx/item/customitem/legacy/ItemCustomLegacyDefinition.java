package org.powernukkitx.item.customitem.legacy;

import org.powernukkitx.item.customitem.ItemCustomVersion;
import org.powernukkitx.item.customitem.ItemCustomDefinition;
import org.powernukkitx.item.customitem.legacy.components.ItemLegacyComponent;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Legacy item definition (version 1).
 * <p>
 * This is the older definition class kept for backward compatibility.
 * New code should use {@link org.powernukkitx.item.customitem.ItemLegacyDefinition}
 * created via {@link org.powernukkitx.item.customitem.ItemLegacyBuilder}.
 */
@Deprecated
class ItemCustomLegacyDefinition extends ItemCustomDefinition {
    ItemCustomLegacyDefinition() {
        super(ItemCustomVersion.LEGACY);
    }

    public void addComponent(ItemLegacyComponent component) {
        this.components.putCompound(component.getIdentifier().getStringId(), component.getTag());
    }

    public void removeComponent(ItemLegacyComponent component) {
        this.components.remove(component.getIdentifier().getStringId());
    }

    @Override
    public @NotNull CompoundTag toNetwork() {
        return components;
    }
}