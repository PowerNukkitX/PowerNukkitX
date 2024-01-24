package cn.nukkit.item;

public class ItemMutton extends ItemEdible {
    public ItemMutton() {
        this(0, 1);
    }

    public ItemMutton(Integer meta) {
        this(meta, 1);
    }

    public ItemMutton(Integer meta, int count) {
        super(MUTTON, meta, count, "Raw Mutton");
    }
}