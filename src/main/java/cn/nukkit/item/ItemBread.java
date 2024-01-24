package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBread extends ItemEdible {

    public ItemBread() {
        this(1);
    }

    public ItemBread(int count) {
        super(BREAD, 0, count);
    }
}
