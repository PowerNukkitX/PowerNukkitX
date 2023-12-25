package cn.nukkit.item;

public class ItemPorkchop extends ItemEdible {
    public ItemPorkchop() {
        this(0, 1);
    }

    public ItemPorkchop(Integer meta) {
        this(meta, 1);
    }

    public ItemPorkchop(Integer meta, int count) {
        super(PORKCHOP, meta, count, "Raw Porkchop");
    }
}