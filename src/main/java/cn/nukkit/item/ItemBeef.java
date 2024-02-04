package cn.nukkit.item;

public class ItemBeef extends ItemFood {
    public ItemBeef() {
        super(BEEF);
    }
    @Override
    public int getFoodRestore() {
        return 3;
    }

    @Override
    public float getSaturationRestore() {
        return 1.8F;
    }
}