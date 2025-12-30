package cn.nukkit.item;

public class ItemCookedBeef extends ItemFood {
    public ItemCookedBeef() {
        super(COOKED_BEEF);
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