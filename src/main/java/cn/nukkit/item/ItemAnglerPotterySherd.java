package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.50-r1")
public class ItemAnglerPotterySherd extends ItemPotterySherd {
    public ItemAnglerPotterySherd() {
        this(0, 1);
    }

    public ItemAnglerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemAnglerPotterySherd(Integer meta, int count) {
        super(ANGLER_POTTERY_SHERD, meta, count, "Angler Pottery Sherd");
    }
}
