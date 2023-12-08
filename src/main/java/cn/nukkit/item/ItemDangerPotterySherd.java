package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.50-r1")
public class ItemDangerPotterySherd extends ItemPotterySherd {
    public ItemDangerPotterySherd() {
        this(0, 1);
    }

    public ItemDangerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemDangerPotterySherd(Integer meta, int count) {
        super(BURN_POTTERY_SHERD, meta, count, "Burn Pottery Sherd");
    }
}
