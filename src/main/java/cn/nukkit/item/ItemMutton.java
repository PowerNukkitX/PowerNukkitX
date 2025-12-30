package cn.nukkit.item;

public class ItemMutton extends ItemFood {
    public ItemMutton() {
        this(0, 1);
    }

    public ItemMutton(Integer meta) {
        this(meta, 1);
    }

    public ItemMutton(Integer meta, int count) {
        super(MUTTON, meta, count, "Raw Mutton");
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 1.2F;
    }
}