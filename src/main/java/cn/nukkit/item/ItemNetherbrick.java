package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemNetherbrick extends Item {

    public ItemNetherbrick() {
        this(0, 1);
    }

    public ItemNetherbrick(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherbrick(Integer meta, int count) {
        super(NETHERBRICK, meta, count, "Nether Brick");
    }
}
