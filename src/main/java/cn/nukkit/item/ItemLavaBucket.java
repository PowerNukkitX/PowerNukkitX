package cn.nukkit.item;

public class ItemLavaBucket extends ItemBucket {
    public ItemLavaBucket() {
        super(LAVA_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 10;
    }

    @Override
    public void setDamage(int meta) {

    }
}