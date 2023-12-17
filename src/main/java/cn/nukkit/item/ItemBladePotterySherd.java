package cn.nukkit.item;


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
