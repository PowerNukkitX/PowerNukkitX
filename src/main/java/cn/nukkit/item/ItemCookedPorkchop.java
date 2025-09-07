package cn.nukkit.item;

public class ItemCookedPorkchop extends ItemFood {
    public ItemCookedPorkchop() {
        super(COOKED_PORKCHOP);
    }

    @Override
    public int getNutrition() {
        return 8;
    }

    @Override
    public float getSaturation() {
        return 12.8F;
    }
}