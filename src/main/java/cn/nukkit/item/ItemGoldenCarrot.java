package cn.nukkit.item;

public class ItemGoldenCarrot extends ItemFood {
    public ItemGoldenCarrot() {
        super(GOLDEN_CARROT);
    }

    @Override
    public int getNutrition() {
        return 6;
    }

    @Override
    public float getSaturation() {
        return 14.4F;
    }
}