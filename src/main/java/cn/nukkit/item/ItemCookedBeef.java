package cn.nukkit.item;

public class ItemCookedBeef extends ItemFood {
    public ItemCookedBeef() {
        super(COOKED_BEEF);
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