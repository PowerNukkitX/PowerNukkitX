package cn.nukkit.item;


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
