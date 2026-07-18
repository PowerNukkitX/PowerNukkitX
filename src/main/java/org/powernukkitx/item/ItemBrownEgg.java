package org.powernukkitx.item;

import org.powernukkitx.entity.ClimateVariant;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBrownEgg extends ItemEgg {

    public ItemBrownEgg() {
        this(0, 1);
    }

    public ItemBrownEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemBrownEgg(Integer meta, int count) {
        super(BROWN_EGG, meta, count, "Brown Egg");
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putString("variant", ClimateVariant.Variant.WARM.getName());
    }
}
