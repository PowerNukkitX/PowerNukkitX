package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class ItemSheafPotterySherd extends ItemPotterySherd {
    public ItemSheafPotterySherd() {
        this(0, 1);
    }

    public ItemSheafPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemSheafPotterySherd(Integer meta, int count) {
        super(SHEAF_POTTERY_SHERD, meta, count, "Sheaf Pottery Sherd");
    }
}
