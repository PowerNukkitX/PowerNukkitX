package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.50-r1")
public class ItemPrizePotterySherd extends ItemPotterySherd {
    public ItemPrizePotterySherd() {
        this(0, 1);
    }

    public ItemPrizePotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemPrizePotterySherd(Integer meta, int count) {
        super(PRIZE_POTTERY_SHERD, meta, count, "Prize Pottery Sherd");
    }
}
