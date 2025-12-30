package cn.nukkit.item;

public class ItemCookedChicken extends ItemFood {
    public ItemCookedChicken() {
        super(COOKED_CHICKEN);
    }

    @Override
    public int getNutrition() {
        return 6;
    }

    @Override
    public float getSaturation() {
        return 7.2F;
    }
}