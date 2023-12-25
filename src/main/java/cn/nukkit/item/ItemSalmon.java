package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSalmon extends ItemFish {
    public ItemSalmon() {
        super(SALMON, 0, 1);
    }

    protected ItemSalmon(String id, Integer meta, int count) {
        super(id, meta, count);
    }
}
