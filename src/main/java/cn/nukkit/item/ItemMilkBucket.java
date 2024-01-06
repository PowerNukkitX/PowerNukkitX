package cn.nukkit.item;

public class ItemMilkBucket extends ItemBucket {
    public ItemMilkBucket() {
        super(MILK_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 1;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}