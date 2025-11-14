package cn.nukkit.item;

import org.jetbrains.annotations.NotNull;

public abstract class ItemHarness extends Item {
    public ItemHarness(@NotNull String id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
