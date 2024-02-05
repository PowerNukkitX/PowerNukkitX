package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBread extends ItemFood {

    public ItemBread() {
        this(1);
    }

    public ItemBread(int count) {
        super(BREAD, 0, count);
    }

    @Override
    public int getFoodRestore() {
        return 5;
    }

    @Override
    public float getSaturationRestore() {
        return 6F;
    }
}
