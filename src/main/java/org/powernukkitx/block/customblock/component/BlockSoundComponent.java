package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Block sound component for custom blocks.
 */
public class BlockSoundComponent implements BlockComponent {
    private String event;

    public BlockSoundComponent() {
    }

    public BlockSoundComponent(String event) {
        this.event = event != null ? event : "";
    }

    public BlockSoundComponent event(String event) {
        this.event = event != null ? event : "";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.BLOCK_SOUND;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("event", event != null ? event : "");
    }
}