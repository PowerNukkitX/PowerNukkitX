package cn.nukkit.item;

public class ItemTropicalFish extends ItemFish {
    public ItemTropicalFish() {
        super(TROPICAL_FISH, 0, 1);
    }

    @Override
    public int getNutrition() {
        return 1;
    }

    @Override
    public float getSaturation() {
        return 0.2F;
    }
}