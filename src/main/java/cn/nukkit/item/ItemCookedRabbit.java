package cn.nukkit.item;

public class ItemCookedRabbit extends ItemFood {
    public ItemCookedRabbit() {
        super(COOKED_RABBIT);
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