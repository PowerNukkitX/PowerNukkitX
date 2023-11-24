package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public class ItemArmsUpPotterySherd extends ItemPotterySherd {
    public ItemArmsUpPotterySherd() {
        this(0, 1);
    }

    public ItemArmsUpPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemArmsUpPotterySherd(Integer meta, int count) {
        super(ARMS_UP_POTTERY_SHERD, meta, count, "Arms Up Pottery Sherd");
    }
}
