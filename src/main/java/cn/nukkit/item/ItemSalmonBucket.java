package cn.nukkit.item;

public class ItemSalmonBucket extends ItemBucket {
    public ItemSalmonBucket() {
        super(SALMON_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 3;
    }

    @Override
    public void setDamage(Integer meta) {

    }
}