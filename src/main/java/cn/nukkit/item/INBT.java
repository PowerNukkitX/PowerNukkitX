package cn.nukkit.item;

import cn.nukkit.Nukkit;
import cn.nukkit.inventory.Inventory;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.jetbrains.annotations.Nullable;

public interface INBT {

    String START_TIME_KEY = "StartTime";

    NbtMap getNamedTag();

    Item setNamedTag(@Nullable NbtMap tag);

    boolean hasCompoundTag();

    default void onChange(Inventory inventory) {
        NbtMapBuilder tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.builder();
            setNamedTag(tag.build());
        } else {
            tag = this.getNamedTag().toBuilder();
        }
        if (!tag.containsKey(START_TIME_KEY)) tag.putLong(START_TIME_KEY, -1);
        if (Nukkit.START_TIME != tag.build().getLong(START_TIME_KEY)) {
            tag.putLong(START_TIME_KEY, Nukkit.START_TIME);
        }
        this.setNamedTag(tag.build());
    }
}