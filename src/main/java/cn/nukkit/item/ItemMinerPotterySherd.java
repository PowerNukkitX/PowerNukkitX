package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class ItemMinerPotterySherd extends ItemPotterySherd {
    public ItemMinerPotterySherd() {
        this(0, 1);
    }

    public ItemMinerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemMinerPotterySherd(Integer meta, int count) {
        super(MINER_POTTERY_SHERD, meta, count, "Miner Pottery Sherd");
    }
}
