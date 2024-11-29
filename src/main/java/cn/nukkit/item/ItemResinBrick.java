package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemResinBrick extends Item {

    public ItemResinBrick() {
        this(0, 1);
    }

    public ItemResinBrick(Integer meta) {
        this(meta, 1);
    }

    public ItemResinBrick(Integer meta, int count) {
        super(RESIN_BRICK, 0, count, "Resin Brick");
    }
}
