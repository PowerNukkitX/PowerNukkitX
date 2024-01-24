package cn.nukkit.item;

public class ItemCodBucket extends ItemBucket {
    public ItemCodBucket() {
        super(COD_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 2;
    }
}