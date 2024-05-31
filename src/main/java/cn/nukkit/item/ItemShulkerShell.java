package cn.nukkit.item;

public class ItemShulkerShell extends Item {
    /**
     * @deprecated 
     */
    

    public ItemShulkerShell() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemShulkerShell(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemShulkerShell(Integer meta, int count) {
        super(SHULKER_SHELL, meta, count, "Shulker Shell");
    }

}
