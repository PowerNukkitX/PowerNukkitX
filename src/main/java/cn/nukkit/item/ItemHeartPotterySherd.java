package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public class ItemHeartPotterySherd extends ItemPotterySherd {
    public ItemHeartPotterySherd() {
        this(0, 1);
    }

    public ItemHeartPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemHeartPotterySherd(Integer meta, int count) {
        super(HEART_POTTERY_SHERD, meta, count, "Heart Pottery Sherd");
    }
}
