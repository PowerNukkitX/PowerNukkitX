package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Player collision component for custom blocks.
 */
public class PlayerCollisionComponent implements BlockComponent {
    private boolean enabled = true;

    public PlayerCollisionComponent() {
    }

    public PlayerCollisionComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public PlayerCollisionComponent enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.PLAYER_COLLISION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putBoolean("enabled", enabled);
    }
}