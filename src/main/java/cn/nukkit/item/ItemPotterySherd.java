package cn.nukkit.item;


public abstract class ItemPotterySherd extends Item {
    public ItemPotterySherd(String id) {
        super(id);
    }

    public ItemPotterySherd(String id, int count) {
        super(id, 0, count);
    }
}
