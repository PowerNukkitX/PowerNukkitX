package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Max stack size component for custom items.
 */
public class MaxStackSizeComponent implements ItemComponent {
    private int maxStackSize = 64;

    public MaxStackSizeComponent() {
    }

    public MaxStackSizeComponent(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public MaxStackSizeComponent maxStackSize(int value) {
        this.maxStackSize = value;
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.MAX_STACK_SIZE;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putInt("max_stack_size", maxStackSize);
    }
}