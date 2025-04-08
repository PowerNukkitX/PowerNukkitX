package cn.nukkit.item;

import cn.nukkit.Nukkit;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface INBT {

    String START_TIME_KEY = "StartTime";

    CompoundTag getNamedTag();

    Item setNamedTag(@Nullable CompoundTag tag);

    boolean hasCompoundTag();

    default void onChange(Inventory inventory) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
            setNamedTag(tag);
        } else {
            tag = this.getNamedTag();
        }
        if(!tag.contains(START_TIME_KEY)) tag.putLong(START_TIME_KEY, -1);
        if(Nukkit.START_TIME != tag.getLong(START_TIME_KEY)){
            tag.putLong(START_TIME_KEY, Nukkit.START_TIME);
        }
    }

}
