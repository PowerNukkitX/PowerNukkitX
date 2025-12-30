package cn.nukkit.item;

public class ItemCookedMutton extends ItemFood {
    public ItemCookedMutton() {
        super(COOKED_MUTTON);
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