package cn.nukkit.item;

import cn.nukkit.entity.ClimateVariant;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBlueEgg extends ItemEgg {
    public ItemBlueEgg() {
        this(0, 1);
    }

    public ItemBlueEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemBlueEgg(Integer meta, int count) {
        super(BLUE_EGG, meta, count, "Blue Egg");
    }

    @Override
    protected void correctNBT(NbtMap nbt) {
        nbt = nbt.toBuilder().putString("variant", ClimateVariant.Variant.COLD.getName()).build();
    }
}
