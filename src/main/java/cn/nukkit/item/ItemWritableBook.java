package cn.nukkit.item;

/**
 * alias BookAndQuill
 */
public class ItemWritableBook extends ItemBookWritable {
    /**
     * @deprecated 
     */
    
    public ItemWritableBook() {
        super(WRITABLE_BOOK);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }
}