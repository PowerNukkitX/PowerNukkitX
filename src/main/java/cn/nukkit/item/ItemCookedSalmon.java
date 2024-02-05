package cn.nukkit.item;

public class ItemCookedSalmon extends ItemSalmon {
    public ItemCookedSalmon() {
        super(COOKED_SALMON, 0, 1);
    }

    @Override
    public int getFoodRestore() {
        return 6;
    }

    @Override
    public float getSaturationRestore() {
        return 9.6F;
    }
}