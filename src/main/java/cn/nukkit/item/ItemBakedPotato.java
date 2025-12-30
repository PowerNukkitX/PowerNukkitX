package cn.nukkit.item;

public class ItemBakedPotato extends ItemFood {
    public ItemBakedPotato() {
        super(BAKED_POTATO);
    }

    @Override
    public int getNutrition() {
        return 5;
    }

    @Override
    public float getSaturation() {
        return 7.2F;
    }
}