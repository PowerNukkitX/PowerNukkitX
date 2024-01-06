package cn.nukkit.item;

public class ItemPowderSnowBucket extends ItemBucket {
    public ItemPowderSnowBucket() {
        super(POWDER_SNOW_BUCKET);
    }

    public int getBucketType() {
        return 11;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}