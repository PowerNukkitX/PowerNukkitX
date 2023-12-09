package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.50-r1")
public class ItemBladePotterySherd extends ItemPotterySherd {
    public ItemBladePotterySherd() {
        this(0, 1);
    }

    public ItemBladePotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemBladePotterySherd(Integer meta, int count) {
        super(BLADE_POTTERY_SHERD, meta, count, "Blade Pottery Sherd");
    }
}
