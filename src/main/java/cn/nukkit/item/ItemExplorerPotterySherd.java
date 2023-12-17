package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class ItemExplorerPotterySherd extends ItemPotterySherd {
    public ItemExplorerPotterySherd() {
        this(0, 1);
    }

    public ItemExplorerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemExplorerPotterySherd(Integer meta, int count) {
        super(EXPLORER_POTTERY_SHERD, meta, count, "Explorer Pottery Sherd");
    }
}
