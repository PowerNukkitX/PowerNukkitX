package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class ItemBrewerPotterySherd extends ItemPotterySherd {
    public ItemBrewerPotterySherd() {
        this(0, 1);
    }

    public ItemBrewerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemBrewerPotterySherd(Integer meta, int count) {
        super(BREWER_POTTERY_SHERD, meta, count, "Brewery Pottery Sherd");
    }
}
