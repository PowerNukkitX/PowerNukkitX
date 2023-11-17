package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public class ItemHowlPotterySherd extends ItemPotterySherd {
    public ItemHowlPotterySherd() {
        this(0, 1);
    }

    public ItemHowlPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemHowlPotterySherd(Integer meta, int count) {
        super(HOWL_POTTERY_SHERD, meta, count, "Howl Pottery Sherd");
    }
}
