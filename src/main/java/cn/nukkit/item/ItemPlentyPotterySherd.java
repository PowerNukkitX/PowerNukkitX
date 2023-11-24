package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public class ItemPlentyPotterySherd extends ItemPotterySherd {
    public ItemPlentyPotterySherd() {
        this(0, 1);
    }

    public ItemPlentyPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemPlentyPotterySherd(Integer meta, int count) {
        super(PLENTY_POTTERY_SHERD, meta, count, "Plenty Pottery Sherd");
    }
}
