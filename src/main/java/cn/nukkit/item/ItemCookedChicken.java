package cn.nukkit.item;

public class ItemCookedChicken extends ItemFood {
    public ItemCookedChicken() {
        super(COOKED_CHICKEN);
    }

    @Override
    public int getFoodRestore() {
        return 6;
    }

    @Override
    public float getSaturationRestore() {
        return 7.2F;
    }
}