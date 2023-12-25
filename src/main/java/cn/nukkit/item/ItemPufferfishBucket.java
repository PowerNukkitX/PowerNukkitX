package cn.nukkit.item;

public class ItemPufferfishBucket extends ItemBucket {
    public ItemPufferfishBucket() {
        super(PUFFERFISH_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 5;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}