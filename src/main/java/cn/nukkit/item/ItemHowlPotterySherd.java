package cn.nukkit.item;


public class ItemHowlPotterySherd extends ItemPotterySherd {
    public ItemHowlPotterySherd() {
        this(0, 1);
    }

    public ItemHowlPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemHowlPotterySherd(Integer meta, int count) {
        super(HOWL_POTTERY_SHERD, meta, count, "Howl Pottery Sherd");
    }
}
