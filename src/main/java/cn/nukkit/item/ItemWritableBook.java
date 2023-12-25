package cn.nukkit.item;

/**
 * alias BookAndQuill
 */
public class ItemWritableBook extends ItemBookWritable {
    public ItemWritableBook() {
        super(WRITABLE_BOOK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}