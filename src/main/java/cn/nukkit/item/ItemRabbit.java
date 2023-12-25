package cn.nukkit.item;

public class ItemRabbit extends ItemEdible {
    public ItemRabbit() {
        this(0, 1);
    }

    public ItemRabbit(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbit(Integer meta, int count) {
        super(RABBIT, meta, count, "Raw Rabbit");
    }
}