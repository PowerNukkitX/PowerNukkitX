package cn.nukkit.item;


public class ItemMournerPotterySherd extends ItemPotterySherd {
    public ItemMournerPotterySherd() {
        this(0, 1);
    }

    public ItemMournerPotterySherd(Integer meta) {
        this(meta, 1);
    }
    public ItemMournerPotterySherd(Integer meta, int count) {
        super(MOURNER_POTTERY_SHERD, meta, count, "Mourner Pottery Sherd");
    }
}
