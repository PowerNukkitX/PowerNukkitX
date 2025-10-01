package cn.nukkit.item.utils;

import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public final class ShooterAmmo {
    private final String itemId;
    private boolean useOffhand = true;
    private boolean searchInventory = true;
    private boolean useInCreative = true;

    private ShooterAmmo(@NotNull String itemId) {
        this.itemId = itemId;
    }

    public static ShooterAmmo set(@NotNull String itemId) {
        return new ShooterAmmo(itemId);
    }

    public ShooterAmmo useOffhand(boolean v) { this.useOffhand = v; return this; }

    public ShooterAmmo searchInventory(boolean v) { this.searchInventory = v; return this; }

    public ShooterAmmo useInCreative(boolean v) { this.useInCreative = v; return this; }

    public CompoundTag toNbt() {
        return new CompoundTag()
                .putCompound("item", new CompoundTag().putString("name", itemId))
                .putByte("use_offhand",      (byte) (useOffhand ? 1 : 0))
                .putByte("search_inventory", (byte) (searchInventory ? 1 : 0))
                .putByte("use_in_creative",  (byte) (useInCreative ? 1 : 0));
    }
}
