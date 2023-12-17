package cn.nukkit.item;


public class ItemDangerPotterySherd extends ItemPotterySherd {
    public ItemDangerPotterySherd() {
        this(0, 1);
    }

    public ItemDangerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemDangerPotterySherd(Integer meta, int count) {
        super(BURN_POTTERY_SHERD, meta, count, "Burn Pottery Sherd");
    }
}
