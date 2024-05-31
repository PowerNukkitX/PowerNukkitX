package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCarrot extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemCarrot() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCarrot(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCarrot(Integer meta, int count) {
        super(CARROT, 0, count, "Carrot");
        this.block = Block.get(BlockID.CARROTS);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 4.8F;
    }
}
