package cn.nukkit.item;

public class ItemCookedRabbit extends ItemFood {
    public ItemCookedRabbit() {
        super(COOKED_RABBIT);
    }

    @Override
    public int getNutrition() {
        return 5;
    }

    @Override
    public float getSaturation() {
        return 6F;
    }
}