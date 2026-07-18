package org.powernukkitx.item.customitem;

import org.powernukkitx.nbt.tag.CompoundTag;

class ItemCustomDefinition {
    private final ItemCustomVersion version;
    protected final CompoundTag components = new CompoundTag();

    ItemCustomDefinition(ItemCustomVersion version) {
        this.version = version;
    }

    public ItemCustomVersion getVersion() {
        return version;
    }

    public CompoundTag getComponents() {
        return components.copy();
    }
}
