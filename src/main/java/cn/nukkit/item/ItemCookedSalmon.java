package cn.nukkit.item;

public class ItemCookedSalmon extends ItemSalmon {
    public ItemCookedSalmon() {
        super(COOKED_SALMON, 0, 1);
    }

    @Override
    public int getNutrition() {
        return 6;
    }

    @Override
    public float getSaturation() {
        return 9.6F;
    }
}