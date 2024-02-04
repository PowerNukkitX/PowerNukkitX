package cn.nukkit.item;

public class ItemCookedCod extends ItemCod {
    public ItemCookedCod() {
        super(COOKED_COD, 0, 1);
    }

    @Override
    public int getFoodRestore() {
        return 5;
    }

    @Override
    public float getSaturationRestore() {
        return 6F;
    }
}