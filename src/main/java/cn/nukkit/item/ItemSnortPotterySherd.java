package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.50-r1")
public class ItemSnortPotterySherd extends ItemPotterySherd {
    public ItemSnortPotterySherd() {
        this(0, 1);
    }

    public ItemSnortPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemSnortPotterySherd(Integer meta, int count) {
        super(SNORT_POTTERY_SHERD, meta, count, "Snort Pottery Sherd");
    }
}
