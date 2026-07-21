package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.StringTag;

/**
 * Breathability component for custom blocks.
 * Defines whether entities can breathe through the block.
 */
public class BreathabilityComponent implements BlockComponent {
    private String value = "solid";

    public BreathabilityComponent() {
    }

    public BreathabilityComponent(String value) {
        this.value = (value != null && !value.isBlank()) ? value : "solid";
    }

    public BreathabilityComponent solid() {
        this.value = "solid";
        return this;
    }

    public BreathabilityComponent transparent() {
        this.value = "transparent";
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.BREATHABILITY;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putString("value", value);
    }
}