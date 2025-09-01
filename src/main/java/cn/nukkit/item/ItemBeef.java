package cn.nukkit.item;

public class ItemBeef extends ItemFood {
    public ItemBeef() {
        super(BEEF);
    }
    @Override
    public int getNutrition() {
        return 3;
    }

    @Override
    public float getSaturation() {
        return 1.8F;
    }
}