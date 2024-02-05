package cn.nukkit.item;

public class ItemCookedMutton extends ItemFood {
    public ItemCookedMutton() {
        super(COOKED_MUTTON);
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