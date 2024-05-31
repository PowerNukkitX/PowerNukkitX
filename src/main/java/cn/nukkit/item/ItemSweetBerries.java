package cn.nukkit.item;

import cn.nukkit.block.BlockSweetBerryBush;

public class ItemSweetBerries extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemSweetBerries() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSweetBerries(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSweetBerries(Integer meta, int count) {
        super(SWEET_BERRIES, meta, count, "Sweet Berries");
        this.block = new BlockSweetBerryBush();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 0.4F;
    }
}
