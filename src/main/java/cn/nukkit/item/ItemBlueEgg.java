package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBlueEgg extends ItemEgg {
    public ItemBlueEgg() {
        this(0, 1);
    }

    public ItemBlueEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemBlueEgg(Integer meta, int count) {
        super(BLUE_EGG, meta, count, "Blue Egg");
    }
}
