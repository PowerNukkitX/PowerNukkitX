package cn.nukkit.item;

public class ItemAxolotlBucket extends ItemBucket {
    public ItemAxolotlBucket() {
        super(AXOLOTL_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 12;
    }
}