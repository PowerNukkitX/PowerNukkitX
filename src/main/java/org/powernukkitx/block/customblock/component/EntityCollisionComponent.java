package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Entity collision component for custom blocks.
 */
public class EntityCollisionComponent implements BlockComponent {
    private boolean enabled = true;

    public EntityCollisionComponent() {
    }

    public EntityCollisionComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public EntityCollisionComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.ENTITY_COLLISION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}