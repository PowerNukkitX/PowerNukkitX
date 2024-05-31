package cn.nukkit.item;

public class ItemEnchantedBook extends Item {
    /**
     * @deprecated 
     */
    
    public ItemEnchantedBook() {
        super(ENCHANTED_BOOK);
    }

    
    /**
     * @deprecated 
     */
    protected ItemEnchantedBook(String id) {
        super(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean applyEnchantments() {
        return false;
    }
}