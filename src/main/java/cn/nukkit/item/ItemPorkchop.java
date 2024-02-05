package cn.nukkit.item;

public class ItemPorkchop extends ItemFood {
    public ItemPorkchop() {
        this(0, 1);
    }

    public ItemPorkchop(Integer meta) {
        this(meta, 1);
    }

    public ItemPorkchop(Integer meta, int count) {
        super(PORKCHOP, meta, count, "Raw Porkchop");
    }

    @Override
    public int getFoodRestore() {
        return 3;
    }

    @Override
    public float getSaturationRestore() {
        return 1.8F;
    }
}