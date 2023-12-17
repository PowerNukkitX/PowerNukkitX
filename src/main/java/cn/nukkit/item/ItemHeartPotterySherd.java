package cn.nukkit.item;


public class ItemHeartPotterySherd extends ItemPotterySherd {
    public ItemHeartPotterySherd() {
        this(0, 1);
    }

    public ItemHeartPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemHeartPotterySherd(Integer meta, int count) {
        super(HEART_POTTERY_SHERD, meta, count, "Heart Pottery Sherd");
    }
}
