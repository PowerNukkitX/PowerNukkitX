package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public class ItemArcherPotterySherd extends ItemPotterySherd {
    public ItemArcherPotterySherd() {
        this(0, 1);
    }

    public ItemArcherPotterySherd(Integer meta) {
        this(meta, 1);
    }

    public ItemArcherPotterySherd(Integer meta, int count) {
        super(ARCHER_POTTERY_SHERD, meta, count, "Archer Pottery Sherd");
    }
}
