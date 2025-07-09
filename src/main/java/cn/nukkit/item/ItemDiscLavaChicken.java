package cn.nukkit.item;


public class ItemDiscLavaChicken extends Item {

    public ItemDiscLavaChicken() {
        this(0, 1);
    }

    public ItemDiscLavaChicken(Integer meta) {
        this(meta, 1);
    }

    public ItemDiscLavaChicken(Integer meta, int count) {
        super(MUSIC_DISC_LAVA_CHICKEN, 0, count, "Disc Lava Chicken");
    }
}
