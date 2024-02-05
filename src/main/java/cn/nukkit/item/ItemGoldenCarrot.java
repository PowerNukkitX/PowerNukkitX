package cn.nukkit.item;

public class ItemGoldenCarrot extends ItemFood {
    public ItemGoldenCarrot() {
        super(GOLDEN_CARROT);
    }

    @Override
    public int getFoodRestore() {
        return 6;
    }

    @Override
    public float getSaturationRestore() {
        return 14.4F;
    }
}