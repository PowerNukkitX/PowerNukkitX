package cn.nukkit.item;

public class ItemEnchantedBook extends Item {
    public ItemEnchantedBook() {
        super(ENCHANTED_BOOK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean applyEnchantments() {
        return false;
    }
}