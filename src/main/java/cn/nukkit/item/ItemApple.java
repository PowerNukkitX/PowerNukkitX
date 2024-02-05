package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemApple extends ItemFood {
    public ItemApple() {
        super(APPLE);
    }

    @Override
    public int getFoodRestore() {
        return 4;
    }

    @Override
    public float getSaturationRestore() {
        return 2.4F;
    }
}
