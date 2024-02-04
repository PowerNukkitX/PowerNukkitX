package cn.nukkit.item;

public class ItemBakedPotato extends ItemFood {
    public ItemBakedPotato() {
        super(BAKED_POTATO);
    }

    @Override
    public int getFoodRestore() {
        return 5;
    }

    @Override
    public float getSaturationRestore() {
        return 7.2F;
    }
}