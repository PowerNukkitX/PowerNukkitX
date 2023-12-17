package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class ItemHeartbreakPotterySherd extends ItemPotterySherd {
    public ItemHeartbreakPotterySherd() {
        this(0, 1);
    }

    public ItemHeartbreakPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemHeartbreakPotterySherd(Integer meta, int count) {
        super(HEARTBREAK_POTTERY_SHERD, meta, count, "Heartbreak Pottery Sherd");
    }
}
