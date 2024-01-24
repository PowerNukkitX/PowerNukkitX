package cn.nukkit.item;

public class ItemPoisonousPotato extends ItemEdible {
    public ItemPoisonousPotato() {
        this(0, 1);
    }

    public ItemPoisonousPotato(Integer meta) {
        this(meta, 1);
    }

    public ItemPoisonousPotato(Integer meta, int count) {
        super(POISONOUS_POTATO, meta, count, "Poisonous Potato");
    }
}