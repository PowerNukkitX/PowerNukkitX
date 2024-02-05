package cn.nukkit.item;

public class ItemTropicalFish extends ItemFish {
    public ItemTropicalFish() {
        super(TROPICAL_FISH, 0, 1);
    }

    @Override
    public int getFoodRestore() {
        return 1;
    }

    @Override
    public float getSaturationRestore() {
        return 0.2F;
    }
}