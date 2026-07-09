package cn.nukkit.item;

import cn.nukkit.PowerNukkitX;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface INBT {

    String START_TIME_KEY = "StartTime";

    CompoundTag getNbt();

    Item setNbt(@Nullable CompoundTag tag);

    boolean hasNbt();

    default void onChange(Inventory inventory) {
        CompoundTag tag;
        if (!this.hasNbt()) {
            tag = new CompoundTag();
            setNbt(tag);
        } else {
            tag = this.getNbt();
        }
        if (!tag.contains(START_TIME_KEY)) tag.putLong(START_TIME_KEY, -1);
        if (PowerNukkitX.START_TIME != tag.getLong(START_TIME_KEY)) {
            tag.putLong(START_TIME_KEY, PowerNukkitX.START_TIME);
        }
        this.setNbt(tag);
    }
}
