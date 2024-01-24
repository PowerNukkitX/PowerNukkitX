package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBook extends Item {
    public ItemBook() {
        super(BOOK);
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}
