package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBrownEgg extends ItemEgg {

    public ItemBrownEgg() {
        this(0, 1);
    }

    public ItemBrownEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemBrownEgg(Integer meta, int count) {
        super(BROWN_EGG, meta, count, "Brown Egg");
    }
}
