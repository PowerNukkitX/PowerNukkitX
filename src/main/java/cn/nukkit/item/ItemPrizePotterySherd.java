package cn.nukkit.item;


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
