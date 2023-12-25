package cn.nukkit.item;

public class ItemPowderSnowBucket extends ItemBucket {
    public ItemPowderSnowBucket() {
        super(POWDER_SNOW_BUCKET);
    }

    public int getBucketType() {
        return 11;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}