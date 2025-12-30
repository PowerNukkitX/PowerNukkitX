package cn.nukkit.item;

/**
 * ItemFish
 */
public class ItemCod extends ItemFish {
    public ItemCod() {
        super(COD, 0, 1);
    }

    protected ItemCod(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 0.4F;
    }
}