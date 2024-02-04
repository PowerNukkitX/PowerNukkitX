package cn.nukkit.item;

public class ItemCookedPorkchop extends ItemFood {
    public ItemCookedPorkchop() {
        super(COOKED_PORKCHOP);
    }

    @Override
    public int getFoodRestore() {
        return 8;
    }

    @Override
    public float getSaturationRestore() {
        return 12.8F;
    }
}