package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPotato extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemPotato() {
        this(POTATO, 0, 1, "Potato");
    }
    /**
     * @deprecated 
     */
    

    public ItemPotato(Integer meta) {
        this(POTATO, meta, 1,"Potato");
    }
    /**
     * @deprecated 
     */
    

    public ItemPotato(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
        this.block = Block.get(BlockID.POTATOES);
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
        return 0.6F;
    }
}
