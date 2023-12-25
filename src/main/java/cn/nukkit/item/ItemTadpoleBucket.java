package cn.nukkit.item;

public class ItemTadpoleBucket extends ItemBucket {
    public ItemTadpoleBucket() {
        super(TADPOLE_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 13;
    }
}