package cn.nukkit.item;

public class ItemMelonSlice extends ItemFood {
    public ItemMelonSlice() {
        this(0, 1);
    }

    public ItemMelonSlice(Integer meta) {
        this(meta, 1);
    }

    public ItemMelonSlice(Integer meta, int count) {
        super(MELON_SLICE, meta, count, "Melon Slice");
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