package cn.nukkit.item;

public class ItemNameTag extends Item {
    /**
     * @deprecated 
     */
    

    public ItemNameTag() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNameTag(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNameTag(Integer meta, int count) {
        super(NAME_TAG, meta, count, "Name Tag");
    }

}
