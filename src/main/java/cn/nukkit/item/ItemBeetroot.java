package cn.nukkit.item;

import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBeetroot extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemBeetroot() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBeetroot(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBeetroot(Integer meta, int count) {
        super(BlockID.BEETROOT, meta, count, "Beetroot");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 1.2F;
    }

}
