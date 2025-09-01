package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBeetrootSoup extends ItemFood {

    public ItemBeetrootSoup() {
        this(0, 1);
    }

    public ItemBeetrootSoup(Integer meta) {
        this(meta, 1);
    }

    public ItemBeetrootSoup(Integer meta, int count) {
        super(BEETROOT_SOUP, 0, count, "Beetroot Soup");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getNutrition() {
        return 6;
    }

    @Override
    public float getSaturation() {
        return 7.2F;
    }
}
