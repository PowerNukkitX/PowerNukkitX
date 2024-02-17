package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemAxolotlBucket extends ItemBucket {
    public ItemAxolotlBucket() {
        super(AXOLOTL_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 12;
    }
}